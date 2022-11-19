package com.example.sns_project

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sns_project.databinding.AddPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AddPostFragment: Fragment(R.layout.add_post){
    var uid = Firebase.auth.currentUser?.uid //현재 로그인한 사용자의 uid
    var userEmail = Firebase.auth.currentUser?.email.toString()
    var uriList: ArrayList<Uri> = arrayListOf<Uri>()
    lateinit var binding:AddPostBinding
    companion object {
        //static 변수
        var boardId:Int = 1
        var count:Int = 0
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")

        binding = AddPostBinding.bind(view)

        val dataFormat = SimpleDateFormat("yy-MM-dd-hh:mm:ss")

        //갤러리에서 사진 찾기
        binding.imageSerchButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //추가
            binding.imageLoadText.text = ""
            activityResult.launch(intent)
        }

        //게시글 올리기
        binding.postButton.setOnClickListener {
            //EditText에서 문자열 가져와 HashMap으로 생성

            if(uriList.size == 0) {
                binding.imageLoadText.text = "이미지를 선택하세요."
                return@setOnClickListener
            }
            val currentTime : Long = System.currentTimeMillis() // ms로 반환
            val data = hashMapOf(
                "uid" to uid.toString(),
                "boardId" to boardId.toString(),
                "post" to binding.postText.text.toString(),
                "location" to binding.locationText.text.toString(),
                "time" to dataFormat.format(currentTime).toString()
            )



            val EmailID = userEmail.split("@")
            var boardRef = database.getReference("board")
            //board에 새로운 게시물 추가
            boardRef = boardRef.child(EmailID[0].toString() + "_" + boardId)
            boardRef.setValue(data)

            //user의 boardList 속성에 board 식별자 추가
            var usersRef = database.getReference("users")
            usersRef.child(EmailID[0]).child("boardList").child((boardId-1).toString()).setValue(EmailID[0].toString() + "_" + boardId)

            //이미지 Storge에 업로드
            for (i in 0 until uriList.size!!) { //선택 한 사진수만큼 반복
                uploadImageTOFirebase(uriList[i])
                //user의 imageList 속성에 사진 식별자 추가
                usersRef.child(EmailID[0]).child("imageList").child((count-1).toString()).setValue(boardId.toString() + "_" + (count-1) + ".png")
            }

            //게시물 식별자 증가(다음 게시물을 위해)
            boardId+=1

            findNavController().navigate(R.id.action_addPostFragment_to_homeFragment)
        }
    }

    //갤러리에서 고른 사진들 Uri로 변환
    val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && it.data != null) { //잘 가져온 경우
            //화면에 보여주기
            //Glide.with(this).load(uri).into(binding.findImage)

            val clipData = it?.data?.clipData //Clip형태로 가져온다.
            val clipDataSize = clipData?.itemCount
            uriList = arrayListOf<Uri>()

            if (clipData == null) { //이미지를 하나만 선택할 경우 clipData가 null이 올수 있음
                //얻어온 이미지 uri로 바꿔서 배열에 저장
                val selectedImageUri = it?.data?.data!!
                uriList.add(selectedImageUri) //Uri 저장
            }
            else {
                clipData.let { clipData ->
                    for (i in 0 until clipDataSize!!) { //선택 한 사진수만큼 반복
                        //얻어온 이미지 uri로 바꿔서 배열에 저장
                        val selectedImageUri = clipData.getItemAt(i).uri
                        uriList.add(selectedImageUri) //Uri 저장
                    }
                    binding.imageCountText.text = clipDataSize.toString() + "개 선택"
                }
            }

        }
    }

    //Firebase Storage에 이미지를 업로드 하는 함수.
    fun uploadImageTOFirebase(uri: Uri?) {
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()   //FirebaseStorage 인스턴스 생성
        //파일 이름 생성.
//        var fileName = uid.toString() + "_" + boardId + "_" + LocalDateTime.now() + ".png";
        var fileName = boardId.toString() + "_" + (count++) + ".png";
        //파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하기 위해 참조를 생성.
        //참조는 클라우드 파일을 가리키는 포인터라고 할 수 있음.
        var imagesRef = storage!!.reference.child("images/").child(fileName)    //기본 참조 위치/images/${fileName}
        //이미지 파일 업로드
        imagesRef.putFile(uri!!).addOnSuccessListener {
        }.addOnFailureListener {
            println(it)
        }
    }
}