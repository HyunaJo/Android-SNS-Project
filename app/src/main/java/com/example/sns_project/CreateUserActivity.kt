package com.example.sns_project

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class CreateUserActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    var passwordError : String = "비밀번호가 잘못 입력되었습니다."
    var emailError : String = "중복된 아이디입니다."
    var nicknameError : String = "중복된 닉네임입니다."
    var nicknameRegError : String = "닉네임은 10자 이내의 영문(대/소문자), 숫자를 사용해야합니다."
    var passwordRegError : String = "비밀번호는 10-15자 영문(대/소문자) 숫자를 사용해야합니다."
    val nicknameReg = Regex("^[a-zA-Z0-9]{0,10}$")
    val passwordReg = Regex("^[a-zA-Z0-9]{10,15}$")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.createuser)
        val createUserButton = findViewById<Button>(R.id.createUserButton)
        createUserButton.setOnClickListener {
            //사용자가 입력한 정보를 가져옴
            val userEmail = findViewById<EditText>(R.id.idEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordText).text.toString()
            val passwordCheck = findViewById<EditText>(R.id.passwordCheckText).text.toString()
            val nickname = findViewById<EditText>(R.id.NickNameText).text.toString()
            val birthday = findViewById<EditText>(R.id.birthdayText).text.toString()

            //초기화
            findViewById<TextView>(R.id.passwordfaultText).text = ""
            findViewById<TextView>(R.id.EmailErrorText).text = ""
            findViewById<TextView>(R.id.NicknameErrorText).text = ""

            //입력에 대한 제어
            if (userEmail.equals("") || password.equals("") || passwordCheck.equals("") || birthday.equals("") || nickname.equals("")) { //공백이 입력되었을 경우,
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("입력오류").setMessage("전부 입력이 되지 않았습니다.")
                builder.setPositiveButton("OK") { _, _ -> }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
                return@setOnClickListener
            }

            //아이디, 닉네임 중복
            if (!password.equals(passwordCheck)) {            //비밀번호가 다를 경우
                findViewById<TextView>(R.id.passwordfaultText).text = passwordError
                return@setOnClickListener
            }
            if (!nicknameReg.containsMatchIn(nickname)) {                //닉네임은 10자 이내의 영문(대/소문자), 숫자 제한
                findViewById<TextView>(R.id.NicknameErrorText).text = nicknameRegError
                return@setOnClickListener
            }
            if (!passwordReg.containsMatchIn(password)) {                //비밀번호는 10-15자 영문(대/소문자) 숫자 제한
                findViewById<TextView>(R.id.passwordfaultText).text = passwordRegError
                return@setOnClickListener
            }
            else {
                val db = Firebase.firestore
                /*
                //데이터 읽기(콘솔 로그에 찍은 값)
                db.collection("users")
                    .get()
                    .addOnSuccessListener { documents ->            //성공했을경우,
                        for(document in documents) {
                            System.out.println(document.data["email"])
                            System.out.println(document.data["nickname"])
                            if(document.data["email"]?.equals(userEmail) == true) {
                                findViewById<TextView>(R.id.EmailErrorText).text = emailError
                            }
                            if(document.data["nickname"]?.equals(nickname) == true) {
                                findViewById<TextView>(R.id.NicknameErrorText).text = nicknameError
                            }
                        }
                    }
                 */
                val user = hashMapOf(
                    "email"    to userEmail.lowercase(Locale.getDefault()),
                    "password" to password.lowercase(Locale.getDefault()),
                    "nickname" to nickname.lowercase(Locale.getDefault()),
                    "birthday" to birthday
                )

                    //데이터 쓰기
                db.collection("users")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
