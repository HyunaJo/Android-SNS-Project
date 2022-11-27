package com.example.sns_project

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.regex.Pattern




class CreateUserActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.reference.child("users")
    var userList = ArrayList<User>()

    var passwordError : String = "비밀번호가 잘못 입력되었습니다."
    var passwordRegError : String = "비밀번호는 10-15자 영문(대/소문자) 숫자를 사용해야합니다."
    val passwordReg = Regex("^[a-zA-Z0-9]{10,15}$")

    var nicknameDuplicatedError : String = "중복된 닉네임입니다."
    var nicknameRegError : String = "닉네임은 10자 이내의 영문(대/소문자), 숫자를 사용해야합니다."
    val nicknameReg = Regex("^[a-zA-Z0-9]{0,10}$")

    var emailDuplicatedError : String = "중복된 아이디입니다."
    var emailRegexError : String = "잘못된 아이디 형식입니다. 이메일 형식으로 입력해주세요."

    var birthNumberRegexError : String = "생년월일을 주민번호 앞자리형식으로 입력해주세요."
    val birthNumberReg = Regex("^[0-9]{6}$")        //주민번호 앞자리 정규 표현식

    val defaultImageStoragePath : String = "gs://sns-project-dc395.appspot.com/images/default.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.createuser)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapShot in snapshot.children) {
                    val user = userSnapShot.getValue(User::class.java)
                    userList.add(user!!)
                    System.out.println(user)
                }
            }
        })

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

            if (!password.equals(passwordCheck)) {            //비밀번호가 다를 경우
                findViewById<TextView>(R.id.passwordfaultText).text = passwordError
                return@setOnClickListener
            }

            if(checkIsDuplicatedEmail(userEmail)){
                findViewById<TextView>(R.id.EmailErrorText).text = emailDuplicatedError
                return@setOnClickListener
            }

            if(checkIsDuplicatedNickname(nickname)){
                findViewById<TextView>(R.id.NicknameErrorText).text = nicknameDuplicatedError
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

            val pattern: Pattern = Patterns.EMAIL_ADDRESS
            if(!pattern.matcher(userEmail).matches()) {
                findViewById<TextView>(R.id.EmailErrorText).text = emailRegexError
                return@setOnClickListener
            }

            if (!birthNumberReg.containsMatchIn(birthday)) {            //생년월일입력이 잘못되었을때,
                findViewById<TextView>(R.id.BirthNumberErrorText).text = birthNumberRegexError
                return@setOnClickListener
            }

            else {
                val user = hashMapOf(
                    "email"    to userEmail.lowercase(Locale.getDefault()),
                    "password" to password.lowercase(Locale.getDefault()),
                    "nickname" to nickname.lowercase(Locale.getDefault()),
                    "birthday" to birthday
                )

                val EmailID = userEmail.split("@")
                //userEmail을 키값으로 가지는 사용자
                val userNode = usersRef.child(EmailID[0])
                userNode.setValue(user)

                // Authentication 회원가입(인증에 대한 코드)
                Firebase.auth.createUserWithEmailAndPassword(userEmail, password)

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun checkIsDuplicatedEmail(newEmail:String):Boolean{
        var isDuplicated = false
        for(user in userList){
            System.out.println("existEmail = ${user.email} / newEmail = ${newEmail}")
            if(user.email.equals(newEmail)){
                isDuplicated = true
            }
        }
        return isDuplicated
    }

    private fun checkIsDuplicatedNickname(newNickname:String):Boolean{
        var isDuplicated = false
        for(user in userList){
            System.out.println("existNickname = ${user.nickname} / newNickname = ${newNickname}")
            if(user.nickname.equals(newNickname)){
                isDuplicated = true
            }
        }
        return isDuplicated
    }
}