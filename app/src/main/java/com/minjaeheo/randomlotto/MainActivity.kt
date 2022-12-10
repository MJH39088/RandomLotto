package com.minjaeheo.randomlotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val clearButton : Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    private val addButton : Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val runButton : Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker : NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    // 데이터가 순차적으로 쌓이기 때문에 이런 식으로 리스트를 이용해 xml과 연동가능
    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_1),
            findViewById<TextView>(R.id.tv_2),
            findViewById<TextView>(R.id.tv_3),
            findViewById<TextView>(R.id.tv_4),
            findViewById<TextView>(R.id.tv_5),
            findViewById<TextView>(R.id.tv_6)
        )
    }

    private var didRun = false

    private val pickNumberSet = hashSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()
    }

    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()

            didRun = true

            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true
            }

            Log.d("태그태그", list.toString())
        }
    }

    private fun initAddButton() {
        addButton.setOnClickListener {
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있어요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // numberPicker에 들어있는 값에 동일한 값이 있는지 없는지 검사. true false를 반환한다.
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()

            pickNumberSet.add(numberPicker.value)
        }
    }

    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            // 리스트의 index를 앞에서부터 하나하나 꺼내온다
            numberTextViewList.forEach {
                it.isVisible = false
            }

            didRun = false
        }
    }

    private fun getRandomNumber(): List<Int> {
        //apply를 통해 바로 초기화
        val numberList = mutableListOf<Int>()
            .apply {
                for (i in 1..45) {
                    // 1~45에서 이미 선택된 번호가 나왔다면 add하지않고 다시 돌아간다.
                    if (pickNumberSet.contains(i)) {
                        continue
                    }

                    this.add(i)
                }
            }

        // 리스트 안의 index를 랜덤으로 섞는다.
        numberList.shuffle()

        // numberList안의 0번째부터 5번째까지의 index를 가지고 리스트 작성
        // 이미 선택된 숫자의 셋을 리스트로 변환해주고 6에서 선택된 숫자를 뺀 리스트를 가져옴
        val newList = pickNumberSet.toList() + numberList.subList(0,6 - pickNumberSet.size)

        // sorted()를 통해 오름차순 정렬
        return newList.sorted()
    }
}