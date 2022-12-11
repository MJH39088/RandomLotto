package com.minjaeheo.randomlotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    // 바로 초기화를 위해 by lazy를 사용
    // 초기화 버튼
    private val clearButton : Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    // 번호 추가하기 버튼
    private val addButton : Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    // 자동 생성 시작 버튼
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

    // 수정이 가능한 boolean형 변수
    private var didRun = false

    // 1~45까지 중복된 숫자가 들어가더라도 추가가 되지 않는 Set형 List 추가
    private val pickNumberSet = hashSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // numberPicker의 수를 최소 1, 최대 45까지 설정
        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()
    }

    // runButton을 눌렀을 때의 동작을 메소드화
    private fun initRunButton() {
        runButton.setOnClickListener {
            // runButton을 누를 때 list를 생성한다.
            val list = getRandomNumber()

            // 실행되었는지를 초기화
            didRun = true

            // getRandomNumber() 메소드에서 가져온 값들은 index 값이 넘어오지 않아 forEach 함수만 사용해서는
            // 몇 번째 index 인지를 알 수가 없기 때문에 forEachIndexed 함수를 사용해
            // index를 알려준다. 필요한 이유는 textView들을 초기화 시켜주기 위함
            // forEachIndexed { index(배열의 위치), i(리스트 안의 값을 지정하는 변수) }
            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true

                setNumberBackground(number, textView)
            }

            Log.d("태그태그", list.toString())
        }
    }

    // addButton을 눌렀을 때의 동작을 메소드화
    private fun initAddButton() {
        addButton.setOnClickListener {
            // runButton을 누르면 didRun이 true가 되므로 clearButton을 눌러
            // didRun을 다시 false로 설정해줘야 if를 지나갈 수 있다.
            // 숫자 1~4개를 설정해두고 runButton을 누르고 다시 번호 추가하기를 누를 때의 예외 처리
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 내가 선택한 숫자가 5개가 넘을 경우 Toast를 띄우고 되돌려 보낸다.
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있어요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // numberPicker에 들어있는 값에 동일한 값이 있는지 없는지 검사. true false를 반환한다.
            // true일 경우에 Toast를 띄우고 돌려 보낸다.
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // numberTextViewList안에 들어있는 index를 pickNumberSet의 size로 가져온다.
            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            // textView의 text를 내가 선택한 numberPicker의 값으로 설정한다.
            textView.text = numberPicker.value.toString()

            // 코틀린에선 setbackground말고 바로 background 설정가능.
            // kt파일에서 drawable파일을 가져오려면 context를 가져와야 한다.
            setNumberBackground(numberPicker.value, textView)

            // 그 후 pickNumberSet List에 내가 선택한 numberPicker의 value값을 추가해준다.
            pickNumberSet.add(numberPicker.value)
        }
    }

    private fun setNumberBackground(number:Int, textView: TextView) {
        when(number) {
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 ->textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }

    }

    private fun initClearButton() {
        clearButton.setOnClickListener {
            // list 값 초기화
            pickNumberSet.clear()
            // 리스트의 index를 앞에서부터 하나하나 꺼내온다
            // numberTextViewList의 TextView들을 보이지 않게 한다.
            numberTextViewList.forEach {
                it.isVisible = false
            }

            // 실행되었는지를 초기화
            didRun = false
        }
    }

    // Int형 List를 반환한다.
    private fun getRandomNumber(): List<Int> {
        //apply를 통해 바로 초기화, mutable로 선언했기 때문에 리스트의 값은 바뀔 수 있다.
        val numberList = mutableListOf<Int>()
            .apply {
                for (i in 1..45) {
                    // 1~45에서 이미 내가 선택한 번호를 add 하지않고 다시 돌아간다.
                    if (pickNumberSet.contains(i)) {
                        continue
                    }
                    // 내가 선택한 숫자를 제외한 모든 1~45까지의 숫자가 리스트 안에 들어감.
                    this.add(i)
                }
            }

        // 리스트 안의 index를 랜덤으로 섞는다.
        numberList.shuffle()

        // numberList안의 0번째부터 6번째 미만까지의 index를 가지고 리스트 작성
        // newList는 내가 선택한 숫자(pickNumberSet).toList
        // + 랜덤으로 뽑은 리스트(0부터 6번째 미만 - 내가 선택한 숫자의 사이즈)
        // 이렇게 하면 무조건 6개의 숫자가 고정되게 된다.
        val newList = pickNumberSet.toList() + numberList.subList(0,6 - pickNumberSet.size)

        // sorted()를 통해 오름차순 정렬
        return newList.sorted()
    }
}