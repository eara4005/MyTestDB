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
    // Bindingの設定
    private lateinit var binding : ActivityMainBinding
    // データベースヘルパーオブジェクト
    private val _helper = DatabaseHelper(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lvCocktail.onItemClickListener = ListItemClickListener()

        // Saveボタンが押された時の処理
        binding.btnSave.setOnClickListener {
            // 入力された感想を取得
            val note = binding.etNote.text.toString()

            // データベースヘルパーオブジェクトから、
            // データベース接続オブジェクトを取得
            val db = _helper.writableDatabase

            // リストで選択されたカクテルのメモデータを削除。その後インサート
            // 削除用SQL文字列を用意
            val sqlDelete = "DELETE FROM cocktailmemos WHERE _id = ?"
            // SQL文字列を元にプリペアドステートメントを取得
            // プリペアドステートメント：SQL文で値がいつでも変更できるように、変更する箇所だけ変数のようにした命令文を作る仕組みのこと
            // https://qiita.com/wakahara3/items/d7a3674eecd3b021a21e
            var stmt = db.compileStatement(sqlDelete)
            // 変数のバイド。
            stmt.bindLong(1,_cocktailId.toLong())
            // 削除SQLの実行
            stmt.executeUpdateDelete()

            // インサート用SQL文字列の用意
            val sqlInsert = "INSERT INTO cocktailmemos (_id, name, note) VALUES(?,?,?)"
            // SQL文字列を元にプリペアドステートメントを取得
            stmt = db.compileStatement(sqlInsert)
            // 変数のバイド
            stmt.bindLong(1, _cocktailId.toLong())
            stmt.bindString(2, _cocktailName)
            stmt.bindString(3, note)
            // インサートSQLの実行
            stmt.executeInsert()


            binding.etNote.setText("")
            binding.tvCocktailName.text = getString(R.string.tv_name)
            binding.btnSave.isEnabled = false
        }
    }

    override fun onDestroy() {
        // ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
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

            // データベースヘルパーオブジェクトから、データベース接続オブジェクトを取得する
            val db = _helper.writableDatabase
            // 主キーによる検索SQL文字列の用意
            val sql = "SELECT * FROM cocktailmemos WHERE _id = ${_cocktailId}"
            // SQLの実行
            val cursor = db.rawQuery(sql, null)
            // データベースから取得した値を格納する変数の用意。
            // データがなかった時のために、初期値も用意する。
            var note = ""

            // SQL実行の戻り値である cursorオブジェクトをループさせて、
            // データベース内のデータを取得する。
            while (cursor.moveToNext()){
                // カラムのインデックス値を取得
                val indexNote = cursor.getColumnIndex("note")
                // カラムのインデックス値を元に、実際のデータを取得
                note = cursor.getString(indexNote)
            }

            // 感想のEditTextの各画面部品を取得し、値を反映
            binding.setText.text =  note

        }
    }




}