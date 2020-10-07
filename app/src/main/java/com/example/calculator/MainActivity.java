package com.example.calculator;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


public class MainActivity extends AppCompatActivity {
    private TextView textHistory;
    private TextView textInput;
    private int decimalFlag = 0;
    private int newCalcFlag = 0;
    private boolean checkBracket = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textHistory = findViewById(R.id.txt_history);
        textInput = findViewById(R.id.txt_input);
    }

    public void clearCalculator(View view) {
        textInput.setText("");
        textHistory.setText("");
        decimalFlag = 0;
        newCalcFlag = 0;
        checkBracket = false;
        textInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);

    }

    public void onClickDigit(View view) {
        Button button = (Button) view;
        String input = button.getText().toString();

        exceedLength();

        if (newCalcFlag == 0) {
            if (textInput.getText().toString().contains(".")) {
                if (button.getId() == R.id.btn_decimal) {
                    if (decimalFlag == 0) {
                        textInput.setText(String.format("%s%s", textInput.getText().toString(), input));
                        decimalFlag = 1;
                    }
                } else {
                    textInput.setText(String.format("%s%s", textInput.getText().toString(), input));
                }
            } else {
                textInput.setText(String.format("%s%s", textInput.getText().toString(), input));
                if (button.getId() == R.id.btn_decimal) {
                    decimalFlag = 1;
                }
            }
        } else {
            textInput.setText(input);
            textHistory.setText("");
            newCalcFlag = 0;
        }
    }


    public void onClickOperator(View view) {
        Button button = (Button) view;
        String input = button.getText().toString();
        exceedLength();
        textInput.setText(String.format("%s%s", textInput.getText().toString(), input));
        decimalFlag = 0;
        newCalcFlag = 0;
    }

    public void onClickBackspace(View view) {
        String input = textInput.getText().toString();
        if (!input.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            textInput.setText(input);
        }
        if(input.length() > 10){
            textInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        } else {
            textInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        }
    }

    public void onEvaluation(View view) {
        String input = textInput.getText().toString();
        String newInput = input;
        newInput = newInput.replaceAll(getApplicationContext().getString(R.string.divide), "/")
                .replaceAll(getApplicationContext().getString(R.string.multiply), "*")
                .replaceAll(getApplicationContext().getString(R.string.minus), "-")
                .replaceAll(getApplicationContext().getString(R.string.percent), "%");

        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        String finalResult;

        if (newInput.isEmpty() || newInput.equals(".")) {
            finalResult = "0.0";
        } else {

            try {
                Scriptable scriptable = rhino.initStandardObjects();
                finalResult = rhino.evaluateString(scriptable, newInput, "javascript", 1, null).toString();
            } catch (Exception e) {
                finalResult = "0";
            }
        }
        newCalcFlag = 1;
        textHistory.setText(input);
        textInput.setText(finalResult);

    }

    public void onClickBracket(View view) {
        if (checkBracket) {
            textInput.setText(String.format("%s%s", textInput.getText().toString(), ")"));
            checkBracket = false;

        } else {
            textInput.setText(String.format("%s%s", textInput.getText().toString(), "("));
            checkBracket = true;
        }
    }

    private void exceedLength() {
        if (textInput.getText().toString().length() > 10) {
            textInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        }
    }
}