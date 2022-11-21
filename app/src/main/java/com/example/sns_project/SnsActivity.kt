package com.example.sns_project

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.sns_project.databinding.ActivitySnsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SnsActivity : AppCompatActivity() {
    lateinit var viewModel:SnsViewModel
    private lateinit var appbarc: AppBarConfiguration
    private lateinit var binding : ActivitySnsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(Color.WHITE)

        viewModel = ViewModelProvider(this, SnsViewModelFactory(Firebase.auth.currentUser?.email.toString())).get(SnsViewModel::class.java)
//        System.out.println(Firebase.auth.currentUser?.email.toString())
//        System.out.println(viewModel.userData.value!!.nickname)

        supportActionBar!!.setDisplayShowCustomEnabled(true);
        //Toolbar에 표시되는 제목의 표시 유무를 설정. false로 해야 custom한 툴바의 이름이 화면에 보인다.
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //왼쪽 버튼 사용설정(기본은 뒤로가기)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //왼쪽 버튼 아이콘 변경
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)

        val nhf = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        appbarc = AppBarConfiguration(nhf.navController.graph)
        setupActionBarWithNavController(nhf.navController, appbarc)

        binding.bottomNavigationView2.setupWithNavController(nhf.navController)


        NavigationUI.setupActionBarWithNavController(this, nhf.navController,
            AppBarConfiguration.Builder(R.id.homeFragment, R.id.addPostFragment, R.id.myFeedFragment).build())

        nhf.navController.addOnDestinationChangedListener{
                controller, destination,arguments->
            run {
                when (destination.label) {
                    "HomeFragment","AddPostFragment","MyFeedFragment" -> {
                        binding.toolbarTextView.text = "Gostagram"
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp(appbarc)||super.onSupportNavigateUp()
    }

    fun setToolbarTitle(title:String){ // 툴바 이름 바꾸기
        binding.toolbar.title=title
    }
}