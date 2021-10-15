package local.hal.st31.android.calculator_3schoolsjoint;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String inputVal = ""; // 入力中の値
    private String viewVal = ""; // inputVal を表示用に変更した値
    private ArrayList<String> inputList = new ArrayList<>(); // 中置記法でそれぞれの値を保持
    private ArrayList<String> rpnList = new ArrayList<>(); // 逆ポーランド記法でそれぞれの値を保持
    private ArrayList<String> viewList = new ArrayList<>(); // inputList を表示用に値を変更して保持
    private ArrayList<String> opeStack = new ArrayList<>(); // RPN変換時の演算子用のスタック
    private ArrayList<BigDecimal> numStack = new ArrayList<>(); // 計算時の数値用のスタック
    private int bracketsFlag = 0; // 括弧中かのフラグ(enum: [0: 初期値, 1: 括弧の中, 2: 括弧終わり直後])

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt0).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt1).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt2).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt3).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt4).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt5).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt6).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt7).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt8).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.bt9).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btDot).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btEqual).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btAdd).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btSubtract).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btMultiply).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btDivide).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btClear).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btPercent).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btBracketsStart).setOnClickListener(new ButtonClickListener());
        findViewById(R.id.btBracketsEnd).setOnClickListener(new ButtonClickListener());


    }

    private class ButtonClickListener implements View.OnClickListener {
        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvHistory = findViewById(R.id.tvHistory);

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View view) {
            Button button = (Button) view;

            int id = view.getId();
            String clickBtn =  button.getText().toString();
            switch (id) {
                case R.id.bt0:
                case R.id.bt1:
                case R.id.bt2:
                case R.id.bt3:
                case R.id.bt4:
                case R.id.bt5:
                case R.id.bt6:
                case R.id.bt7:
                case R.id.bt8:
                case R.id.bt9:
                case R.id.btDot:
                    if (bracketsFlag == 2) break;
                    inputVal += clickBtn;
                    viewVal = NumberFormat.getNumberInstance().format(new BigDecimal(inputVal));
                    addTextView();
                    if (bracketsFlag == 1) tvHistory.append(" )");
                    break;
                case R.id.btEqual:
                    if (bracketsFlag == 1) {
                        addList(")");
                    }
                    if(!(inputVal.equals(""))) {
                        addList(clickBtn);
                    }
                    infixNotationToRPN();
                    String result = calculate().toString();
                    inputVal = result;
                    viewVal = NumberFormat.getNumberInstance().format(new BigDecimal(inputVal));
                    tvResult.setText(viewVal);

                    viewList.add(viewVal);
                    addHistoryView("=");
                    bracketsFlag = 0;
                    inputList.clear();
                    viewList.clear();
                    break;
                case R.id.btAdd:
                case R.id.btSubtract:
                case R.id.btMultiply:
                case R.id.btDivide:
                    if(!(inputVal.equals("")) || bracketsFlag == 2) {
                        addList(clickBtn);
                    }
                    if (bracketsFlag == 1) tvHistory.append(" )");
                    break;
                case R.id.btBracketsStart:
                    if (inputVal.equals("")) {
                        bracketsFlag = 1;
                        inputList.add("(");
                        viewList.add("(");
                        addHistoryView("(");
                        tvHistory.append(" )");
                    }
                    break;
                case R.id.btBracketsEnd:
                    if(!(inputVal.equals("")) && bracketsFlag == 1) {
                        addList(clickBtn);
                        bracketsFlag = 2;
                        addHistoryView(")");
                    }
                    break;
                case R.id.btPercent:
                    if(!(inputVal.equals(""))) {
                        BigDecimal p = new BigDecimal(inputVal);
                        inputVal = p.divide(new BigDecimal(100)).toString();
                        viewVal = NumberFormat.getNumberInstance().format(new BigDecimal(inputVal));
                        changeTextView();
                    }
                    break;
                case R.id.btClear:
                    tvResult.setText("");
                    tvHistory.setText("");
                    inputList.clear();
                    viewList.clear();
                    rpnList.clear();
                    opeStack.clear();
                    numStack.clear();
                    inputVal= "";
                    viewVal = "";
                    bracketsFlag = 0;
                    break;
            }
        }

        /**
         * ResultとHistoryへの表示
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void addTextView() {
            tvResult.setText(viewVal);
            addHistoryView("");
        }

        /**
         * Listへの値追加とResultとそれの保持変数の初期化
         * @param ope
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void addList(String ope) {
            if (bracketsFlag == 2) {
                inputList.add(ope);
                viewList.add(ope);

                addHistoryView(ope);

                tvResult.setText("");
                inputVal = "";
                viewVal = "";
                bracketsFlag = 0;
                return;
            }

            inputList.add(inputVal);
            viewList.add(viewVal);
            if (!"=".equals(ope)) {
                inputList.add(ope);
            }
            viewList.add(ope);
            addHistoryView(ope);

            tvResult.setText("");
            inputVal = "";
            viewVal = "";
        }

        /**
         * Historyの表示
         * @param ope
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void addHistoryView(String ope) {
            if ("".equals(ope)) {
                // 現状は％の時のみ仕様
                tvHistory.setText(String.format("%s %s", String.join(" ", viewList), viewVal));
            }
            else {
                tvHistory.setText(String.join(" ", viewList));
            }
        }

        /**
         * TextViewとHistoryの表示を変更する
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void changeTextView() {
            tvResult.setText(inputVal);
            tvHistory.setText(String.format("%s %s", String.join(" ", viewList), viewVal));
        }
    }

    /**
     * 逆ポーランド記法の式を計算する
     * @return BigDecimal
     */
    private BigDecimal calculate() {
        BigDecimal result = new BigDecimal("0");
        for (String s : rpnList) {
            if ( isNumMatch(s) ) {
                numStack.add(new BigDecimal(s));
                continue;
            }

            if ("+".equals(s)) {
                result = numStack.get(numStack.size() - 2).add(numStack.get(numStack.size() - 1));
            }
            else if ("-".equals(s)) {
                result = numStack.get(numStack.size() - 2).subtract(numStack.get(numStack.size() - 1));
            }
            else if ("×".equals(s)) {
                result = numStack.get(numStack.size() - 2).multiply(numStack.get(numStack.size() - 1));
            }
            else if ("÷".equals(s)) {
                result = numStack.get(numStack.size() - 2).divide(numStack.get(numStack.size() - 1));
            }
            numStack.remove(numStack.size() - 2);
            numStack.remove(numStack.size() - 1);
            numStack.add(result);
        }
        return result;
    }

    /**
     * 中置記法を逆ポーランド記法(RPN)に変換する
     */
    private void infixNotationToRPN() {
        for (String s : inputList) {
            if ( isNumMatch(s) ) {
                // 数値の時
                rpnList.add(s);
            }
            else if ("(".equals(s)) {
                opeStack.add(s);
            }
            else if (")".equals(s)) {
                for (int i = opeStack.size() - 1; i >= 0; i--) {
                    if ("(".equals(opeStack.get(i))) {
                        opeStack.remove(i);
                        break;
                    }
                    rpnList.add(opeStack.get(i));
                    opeStack.remove(i);
                }
            }
            else {
                while (opeStack.size() > 0) {
                    if ( opeCompare(s, opeStack.get(opeStack.size() - 1)) ) break;
                    rpnList.add(opeStack.get(opeStack.size() - 1));
                    opeStack.remove(opeStack.size() - 1);
                }
                opeStack.add(s);
            }
        }
        // 残りの符号を出力
        while (opeStack.size() > 0) {
            rpnList.add(opeStack.get(opeStack.size() - 1));
            opeStack.remove(opeStack.size() - 1);
        }
    }

    /**
     * 文字が数値(正負の整数と正負の小数)ならtrue、それ以外はfalseを返す
     * @param s 判定する文字列
     * @return boolean
     */
    private boolean isNumMatch(String s) {
        return java.util.regex.Pattern.compile("^([1-9]\\d*|0)(\\.\\d+)?$|^(-[1-9]\\d*|0)(\\.\\d+)?$").matcher(s).matches();
    }


    /**
     * 演算子の優先順位を判断する
     * トークンの方が優先順位が高い時と優先順位が高い時true、スタックの方が優先順位が高い時falseを返す
     * @param token
     * @param stack
     * @return boolean
     */
    private boolean opeCompare(String token, String stack) {
        int i = opePriority(token) - opePriority(stack);

        return i <= 0;
    }

    /**
     * 演算子に対応した数値を返す
     * @param ope 演算子
     * @return int
     */
    private int opePriority(String ope) {
        if ("×".equals(ope) || "÷".equals(ope)) {
            return 1;
        } else if ("+".equals(ope) || "-".equals(ope)) {
            return 2;
        }
        //  () の時
        return 99;
    }
}