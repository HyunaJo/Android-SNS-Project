package com.example.sns_project

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sns_project.databinding.AddPostBinding
import com.example.sns_project.databinding.UserfeedfragmentLayoutBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class AddPostFragment: Fragment(R.layout.add_post){
    var uid = Firebase.auth.currentUser?.uid //현재 로그인한 사용자의 uid
    var userEmail = Firebase.auth.currentUser?.email.toString()
    var uriList: ArrayList<Uri> = arrayListOf<Uri>()
    lateinit var binding:AddPostBinding
    var uri:Uri? = null
    var count = 0;
    val dataFormat = SimpleDateFormat("yy-MM-dd-hh:mm:ss")
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    lateinit var viewModel:SnsViewModel
    var selectedName = ""
    var OriginBlueColor = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = AddPostBinding.bind(view)

        var boardList = java.util.ArrayList<Board>()
        val myRef = database.getReference("board")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapShot in snapshot.children) {
                    val board = userSnapShot.getValue(Board::class.java)
                    boardList.add(board!!)
                }
                var uid = Firebase.auth.currentUser?.uid.toString()
                for(i in boardList) {
                    if(i.uid.equals(uid)) {
                        count+=1
                    }
                }
            }
        })

        //갤러리에서 사진 찾기
        binding.imageSerchButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
            )
            binding.imageLoadText.text = ""
            activityResult.launch(intent)
        }

        //게시글 올리기
        binding.postButton.setOnClickListener {
            //EditText에서 문자열 가져와 HashMap으로 생성

            if(uri == null) {
                binding.imageLoadText.text = "이미지를 선택하세요."
                return@setOnClickListener
            }

            System.out.println("아오!!!!!!! " + count)
            val EmailID = userEmail.split("@")
            //user의 boardList 속성에 board 식별자 추가
            var usersRef = database.getReference("users")
            usersRef.child(EmailID[0]).child("boardList").child(count.toString()).setValue(EmailID[0].toString() + "_" + count)

            //이미지 Storge에 업로드
            uploadImageTOFirebase(uri)

            findNavController().navigate(R.id.action_addPostFragment_to_homeFragment)
        }
    }

    //갤러리에서 고른 사진 Uri로 변환
    val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if(result.resultCode == RESULT_OK) {
            uri = result.data?.data
            binding.imageSelectText.text = "선택 완료"
        }
        else{
            binding.imageSelectText.text = ""
        }
    }

    //Firebase Storage에 이미지를 업로드 하는 함수.
    fun uploadImageTOFirebase(uri: Uri?) {
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()   //FirebaseStorage 인스턴스 생성
        var fileName = userEmail.split("@")[0] + "_" + count.toString() + ".png";
        var storageRef = storage?.reference?.child("images")?.child(fileName)
        //파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하기 위해 참조를 생성.
        //참조는 클라우드 파일을 가리키는 포인터라고 할 수 있음.
        var imagesRef = storage!!.reference.child("images/").child(fileName)    //기본 참조 위치/images/${fileName}
        //이미지 파일 업로드
        imagesRef.putFile(uri!!).addOnSuccessListener {
            //업로드 완료된 경우 users와 board에 관련 값 저장
            storageRef?.downloadUrl?.addOnSuccessListener { uri ->
                val currentTime : Long = System.currentTimeMillis() // ms로 반환
                val data = hashMapOf(
                    "uid" to uid.toString(),
                    "boardId" to count.toString(),
                    "post" to binding.postText.text.toString(),
                    "location" to binding.locationText.text.toString(),
                    "time" to dataFormat.format(currentTime).toString(),
                    "imageUrl" to uri!!.toString(),
                )

                val EmailID = userEmail.split("@")
                //board에 새로운 게시물 식별자 추가
                var boardRef = database.getReference("board")
                boardRef = boardRef.child(EmailID[0].toString() + "_" + count)
                boardRef.setValue(data)

                //게시물 식별자 증가(다음 게시물을 위해)

                //해당 사용자의 imageList에 imageUri 추가
//                var usersRef = database.getReference("users")
//                usersRef.child(EmailID[0]).child("imageList").child((count-1).toString()).setValue(uri!!.toString())
            }

        }.addOnFailureListener {
            println(it)
        }
    }
}