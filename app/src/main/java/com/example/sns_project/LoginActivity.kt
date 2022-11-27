package com.example.sns_project

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Toast
import com.example.sns_project.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        if(isOnline(applicationContext)){
            //회원가입 클릭
            binding.signup.setOnClickListener {
                startActivity(
                    Intent(this, CreateUserActivity::class.java) //회원가입 화면으로 이동
                )
            }
        }

    }

    //로그인 인증 메소드
    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { // it: Task<AuthResult!>
                if(isOnline(applicationContext)){
                    if (it.isSuccessful) { //로그인 성공한 경우
                        startActivity(
                            Intent(this, MainActivity::class.java) //메인 화면으로 이동
                        )
                        finish()
                    }
                    else { //로그인 실패한 경우
                        binding.loginExceptionText.text = "아이디 또는 비밀번호를 다시 확인하세요."
                        Log.w("LoginActivity", "signInWithEmail", it.exception)
                    }
                }
            }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        Toast.makeText(this, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
        return false
    }
}