package local.hal.st31.android.calculator_3schoolsjoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<BigDecimal> numList = new ArrayList<>();
    private ArrayList<String> opeList = new ArrayList<>();
    private String inputVal = "";

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
//        findViewById(R.id.btPercent).setOnClickListener(new ButtonClickListener());
//        findViewById(R.id.btBracketsStart).setOnClickListener(new ButtonClickListener());
//        findViewById(R.id.btBracketsEnd).setOnClickListener(new ButtonClickListener());


    }

    private class ButtonClickListener implements View.OnClickListener {
        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvHistory = findViewById(R.id.tvHistory);

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
                    addTextView(clickBtn);
                    inputVal += clickBtn;
                    break;
                case R.id.btAdd:
                case R.id.btSubtract:
                case R.id.btMultiply:
                case R.id.btDivide:
                    if(!(inputVal.equals(""))) {
                        addList(inputVal, clickBtn);
                    }
                    break;
                case R.id.btEqual:
                    if(!(inputVal.equals(""))) {
                        addList(inputVal, clickBtn);
                    }
                    String result = calculate().toString();
                    tvResult.setText(result);
                    inputVal = result;

                    numList.clear();
                    opeList.clear();
                    break;
                case R.id.btClear:
                    tvResult.setText("");
                    tvHistory.setText("");
                    numList.clear();
                    opeList.clear();
                    inputVal= "";
                    break;
            }
        }

        private void addTextView(String addStr) {
            tvResult.append(addStr);
            if ("=".equals(addStr)) {
                tvHistory.setText("(" + tvHistory.getText().toString() + ")");
            }
            else {
                tvHistory.append(addStr);
            }
        }

        private void addList(String strNum, String ope) {
            addTextView(ope);
            numList.add(new BigDecimal(strNum));
            opeList.add(ope);
            inputVal = "";
        }
    }

    private BigDecimal calculate() {
        int i = 0;

        while(i < opeList.size()) {
            if("×".equals(opeList.get(i)) || "÷".equals(opeList.get(i))) {
                BigDecimal resultMultiDiv = "×".equals(opeList.get(i)) ? numList.get(i).multiply(numList.get(i+1)) : numList.get(i).divide(numList.get(i+1));

                numList.set(i, resultMultiDiv);
                numList.remove(i+1);
                opeList.remove(i);
                i--;
            }
            else if("-".equals(opeList.get(i))) {
                opeList.set(i, "+");
                numList.set(i+1, numList.get(i+1).negate());
            }
            i++;
        }

        BigDecimal result = new BigDecimal("0");
        for(BigDecimal j : numList) {
            result = result.add(j);
        }

        return result;
    }
}