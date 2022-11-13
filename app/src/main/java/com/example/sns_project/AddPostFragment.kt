package com.example.sns_project

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.AddPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime

class AddPostFragment: Fragment(R.layout.add_post){
    var uid = Firebase.auth.currentUser?.uid //현재 로그인한 사용자의 uid
    var uriList: ArrayList<Uri> = arrayListOf<Uri>()
    lateinit var binding:AddPostBinding
    companion object {
        //static 변수
        var boardId:Int = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Firebase.firestore

        val snsActivity = activity as SnsActivity
        snsActivity.showUpButton()

        binding = AddPostBinding.bind(view)

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

            val data = hashMapOf(
                "uid" to uid.toString(),
                "post" to binding.postText.text.toString(),
                "location" to binding.locationText.text.toString()
            )

            //이미지 Storge에 업로드
            for (i in 0 until uriList.size!!) { //선택 한 사진수만큼 반복
                uploadImageTOFirebase(uriList[i])
            }

            //게시글, 위치 Firestore에 업로드
            db.collection("Board")
                .add(data)
                .addOnSuccessListener {
                    // 성공할 경우
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                }

            //게시물 식별자 증가(다음 게시물을 위해)
            boardId+=1
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
        var fileName = uid.toString() + "_" + boardId + "_" + LocalDateTime.now() + ".png";
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