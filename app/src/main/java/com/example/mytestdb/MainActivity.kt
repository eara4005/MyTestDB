package com.example.mytestdb
/*
* データベースのテスト
* いずれは、サブアクティビティに突っ込む*/


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.example.mytestdb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // 選択されたカクテルの主キーIDを表すプロパティ
    private var _cocktailId = -1
    // 選択されたカクテル名を表すプロパティ
    private var _cocktailName = ""

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lvCocktail.onItemClickListener = ListItemClickListener()

        // Saveボタンが押された時の処理
        binding.btnSave.setOnClickListener {
            binding.etNote.setText("")
            binding.tvCocktailName.text = getString(R.string.tv_name)
            binding.btnSave.isEnabled = false
        }
    }

    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long){
            // タップされた行番号をプロパティの主キーIDに代入
            _cocktailId = position
            // タップされた行のデータを取得。これがカクテル名になるので、プロパティに代入
            _cocktailName = parent.getItemAtPosition(position) as String
            // カクテル名を表示する
            binding.tvCocktailName.text = _cocktailName
            // 保存ボタンの有効化
            binding.btnSave.isEnabled = true
        }
    }
}