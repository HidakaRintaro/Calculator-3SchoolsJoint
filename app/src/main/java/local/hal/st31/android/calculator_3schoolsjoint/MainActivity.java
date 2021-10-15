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

    private String inputVal = "";
    private String viewVal = "";
    private ArrayList<String> inputList = new ArrayList<>(); // 中置記法で式を保持
    private ArrayList<String> rpnList = new ArrayList<>(); // 逆ポーランド記法で式を保持
    private ArrayList<String> viewList = new ArrayList<>(); // 表示用の中置記法で式を保持したリスト
    private ArrayList<String> opeStack = new ArrayList<>(); // RPN変換時の演算子用のスタック
    private ArrayList<BigDecimal> numStack = new ArrayList<>(); // 計算時の数値用のスタック

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
                    inputVal += clickBtn;
                    viewVal = NumberFormat.getNumberInstance().format(new BigDecimal(inputVal));
                    addTextView();
                    break;
                case R.id.btEqual:
                    if(!(inputVal.equals(""))) {
                        addList(clickBtn);
                    }
                    infixNotationToRPN();
                    String result = calculate().toString();
                    inputVal = result;
                    viewVal = NumberFormat.getNumberInstance().format(new BigDecimal(inputVal));
                    tvResult.setText(viewVal);
                    break;
                case R.id.btAdd:
                case R.id.btSubtract:
                case R.id.btMultiply:
                case R.id.btDivide:
                    if(!(inputVal.equals(""))) {
                        addList(clickBtn);
                    }
                    break;
                // （を入力したら）の未入力を防ぐために自動入力にしたい
//                case R.id.btBracketsStart:
//                case R.id.btBracketsEnd:

                case R.id.btPercent:
                    if(!(inputVal.equals(""))) {
                        BigDecimal p = new BigDecimal(inputVal);
                        inputVal = p.divide(new BigDecimal(100)).toString();
                        viewVal = NumberFormat.getNumberInstance().format(new BigDecimal(inputVal));
                        changeTextView(viewVal);
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
                    break;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void addTextView() {
            tvResult.setText(viewVal);
            addHistoryView("");
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void addList(String ope) {
            inputList.add(inputVal);
            viewList.add(viewVal);
            if (!"=".equals(ope)) {
                inputList.add(ope);
                viewList.add(ope);
            }
            addHistoryView(ope);

            tvResult.setText("");
            inputVal = "";
            viewVal = "";
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void addHistoryView(String ope) {
            if ("=".equals(ope)) {
                tvHistory.setText(String.format("(%s)", String.join(" ", viewList)));
            }
            else if ("".equals(ope)) {
                tvHistory.setText(String.format("%s %s", String.join(" ", viewList), viewVal));
            }
            else {
                tvHistory.setText(String.join(" ", viewList));
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void changeTextView(String changeStr) {
            tvResult.setText(changeStr);
            tvHistory.setText(String.format("%s %s", String.join(" ", viewList), viewVal));
        }
    }

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
     * 文字が数値(正負の整数と正負の小数)かどうか判断する
     * @param s 判定する文字列
     * @return boolean
     */
    private boolean isNumMatch(String s) {
        return java.util.regex.Pattern.compile("^([1-9]\\d*|0)(\\.\\d+)?$|^(-[1-9]\\d*|0)(\\.\\d+)?$").matcher(s).matches();
    }


    private boolean opeCompare(String token, String stack) {
        int i = opePriority(token) - opePriority(stack);

        return i <= 0;
    }

    private int opePriority(String ope) {
        if ("×".equals(ope) || "÷".equals(ope)) {
            return 1;
        }
        else if ("+".equals(ope) || "-".equals(ope)) {
            return 2;
        }
        //  () の時
        return 99;
    }
}