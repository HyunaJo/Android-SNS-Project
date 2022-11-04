package com.example.sns_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import com.example.sns_project.databinding.LoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: LoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //회원가입 텍스트에 밑줄추가
        var content:SpannableString = SpannableString(binding.signup.text);
        content.setSpan(UnderlineSpan(),0,content.length,0);
        binding.signup.setText(content)

        //로그인 버튼 클릭
        binding.loginButton.setOnClickListener {
            val userEmail = binding.email.text.toString()
            val password = binding.password.text.toString()
            if(userEmail.equals("") == true) { //아이디 공백 입력
                binding.loginExceptionText.text = "아이디를 입력하세요."
                return@setOnClickListener
            }
            else if(password.equals("") == true) { //비밀번호 공백 입력
                binding.loginExceptionText.text = "비밀번호를 입력하세요."
                return@setOnClickListener
            }
            doLogin(userEmail, password)
        }

        //회원가입 클릭
        binding.signup.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java) //회원가입 화면으로 이동
            )
        }
    }

    //로그인 인증 메소드
    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { // it: Task<AuthResult!>
                if (it.isSuccessful) { //로그인 성공한 경우
                    startActivity(
                        Intent(this, MainActivity::class.java) //메인 화면으로 이동
                    )
                    finish()
                } else { //로그인 실패한 경우
                    binding.loginExceptionText.text = "아이디 또는 비밀번호를 다시 확인하세요."
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                }
            }
    }
}