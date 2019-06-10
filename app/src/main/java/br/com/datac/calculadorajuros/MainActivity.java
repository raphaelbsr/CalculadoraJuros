package br.com.datac.calculadorajuros;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.text.Editable;
import android.content.Context;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etValorEmprestimo;
    private EditText etJuros;
    private EditText etQuantidadeParcelas;
    private EditText etValorParcela;
    private Button btnCalcular;
    private Button btnLimpar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etValorEmprestimo = findViewById(R.id.editTextValorEmprestimo);
        etJuros = findViewById(R.id.editTextJuros);
        etQuantidadeParcelas = findViewById(R.id.editTextQuantidadeParcelas);
        etValorParcela = findViewById(R.id.editTextValorParcela);

        btnCalcular = findViewById(R.id.buttonCalcular);
        btnLimpar = findViewById(R.id.buttonLimpar);

        etValorEmprestimo.addTextChangedListener(new MoneyTextWatcher(etValorEmprestimo, this));
        etValorParcela.addTextChangedListener(new MoneyTextWatcher(etValorParcela, this));
        //etJuros.addTextChangedListener(new DiscountTextWatcher(etJuros, this));

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etValorEmprestimo.setText("");
                etValorParcela.setText("");
                etJuros.setText("");
                etQuantidadeParcelas.setText("");
            }
        });

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkFields()){

                    DecimalFormat df = new DecimalFormat("0.00");

                    if(etQuantidadeParcelas.getText().toString().equals("")){
                        double quantidadeParcelas = calcularQuantidadeParcelas(convertJuros(),convertValorParcela(),convertValorEmprestimo());
                        etQuantidadeParcelas.setText(String.valueOf(df.format(quantidadeParcelas)));
                    }else if( etJuros.getText().toString().equals("")){
                        double juros = calcularJuros(convertParcela(),convertValorParcela(),convertValorEmprestimo());
                        etJuros.setText(String.valueOf(df.format(juros)));
                    }else if(etValorParcela.getText().toString().equals("")){
                        double valorParcela = calcularValorParcela(convertValorEmprestimo(),convertJuros(),convertParcela());
                        etValorParcela.setText(String.valueOf(df.format(valorParcela)));
                    }else{
                        double valorEmprestimo = calcularValorEmprestimo(convertParcela(),convertJuros(),convertValorParcela());
                        etValorEmprestimo.setText(String.valueOf(df.format(valorEmprestimo)));
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Preencha três valores", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private double convertValorEmprestimo(){
        return Double.valueOf(etValorEmprestimo.getText()
                .toString()
                .replace(" ","")
                .replaceAll("\\.", "")
                .replaceAll(",","."));
    }

    private double convertJuros(){
        return Double.valueOf(etJuros.getText().toString());
    }

    private double convertParcela(){
        return Double.valueOf(etQuantidadeParcelas.getText().toString());
    }

    private double convertValorParcela(){
        return Double.valueOf(etValorParcela.getText()
                .toString()
                .replace(" ","")
                .replaceAll("\\.", "")
                .replaceAll(",","."));
    }

    private boolean checkFields(){

        int f1 = etQuantidadeParcelas.getText().toString().equals("") ? 0 : 1;
        int f2 = etJuros.getText().toString().equals("") ? 0 : 1;
        int f3 = etValorParcela.getText().toString().equals("") ? 0 : 1;
        int f4 = etValorEmprestimo.getText().toString().equals("") ? 0 : 1;

        if((f1+f2+f3+f4) == 3){
            return true;
        }else{
            return false;
        }

    }

    private double calcularValorParcela(double valorEmprestimo, double juros, double parcelas){
        double C = valorEmprestimo;
        double I = juros;
        double N = parcelas;
        I = I / 100;
        double P = (C * I) / (1 - (1 / Math.pow((1 + I), N)));
        return P;
    }

    private double calcularQuantidadeParcelas(double juros, double valorParcela, double valorEmprestimo){
        double C = valorEmprestimo;
        double P = valorParcela;
        double I = juros;
        I = I / 100;
        double K = 1 - ((C * I) / P);
        double N = Math.log(K) / -Math.log(1 + I);
        return (N);
    }

    private double calcularJuros(double parcelas, double valorParcela, double valorEmprestimo){
        double CT = 0;
        double C = valorEmprestimo;
        double P = valorParcela;
        double N = parcelas;
        double K = P / C;
        double I = 0.00001;
        if (K > CT) {
            while (K > CT) {
                I = I + 0.00001;
                CT = (I * Math.pow((1 + I), N)) / (Math.pow((1 + I), N) - 1);
            }
        } else {
            while (CT > K) {
                I = I + 0.00001;
                CT = (I * Math.pow((1 + I), N)) / (Math.pow((1 + I), N) - 1);
            }
        }
        I = I * 100;
        return (I);
    }

    private double calcularValorEmprestimo(double parcelas, double juros, double valorParcela){
        double P = valorParcela;
        double I = juros;
        double N = parcelas;
        I = I / 100;
        double C = (P / I) * (Math.pow((1 + I), N) - 1) / (Math.pow((1 + I), N));
        return (C);
    }

    public class MoneyTextWatcher implements TextWatcher {

        private final WeakReference<EditText> editTextWeakReference;
        private Context context;
        public MoneyTextWatcher(EditText editText,Context context) {
            editTextWeakReference = new WeakReference<EditText>(editText);
            this.context = context;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //ListaProdutoActivity.ignoreUnitarioTextChange = true;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.d("DESCONTO","MONEY WHATCHER");
            try {
                EditText editText = editTextWeakReference.get();
                if (editText == null) return;
                String s = editable.toString();
                editText.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[ ,.]", "");
                BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(parsed);
                formatted = formatted.replace("R$", "");
                formatted = formatted.replace("$", "");
                editText.setText(formatted);
                editText.setSelection(formatted.length());
                editText.addTextChangedListener(this);
            }catch (NumberFormatException ex){
                ex.printStackTrace();
            }
        }
    }

    public class DiscountTextWatcher implements TextWatcher {

        private final WeakReference<EditText> editTextWeakReference;
        private WeakReference<DiscountTextWatcher> textWatcherWeakReference;
        String current = "";
        Context context;

        public DiscountTextWatcher(EditText editText, Context context) {
            this.editTextWeakReference = new WeakReference<EditText>(editText);
            this.context = context;
        }

        public void setTextWatcherWeakReference(DiscountTextWatcher textWatcherWeakReference) {
            this.textWatcherWeakReference = new WeakReference<DiscountTextWatcher>(textWatcherWeakReference);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //ListaProdutoActivity.ignoreUnitarioTextChange = true;
        }

        @Override
        public void onTextChanged(CharSequence seq, int start, int before, int count) {

            if (!seq.toString().equals(current)) {
                try {
                    EditText editText = editTextWeakReference.get();

                    if(textWatcherWeakReference == null){return;}

                    DiscountTextWatcher discount = textWatcherWeakReference.get();
                    if (editText == null) return;
                    String s = seq.toString();

                    if (!s.contains(",")) {
                        s += ",00";
                    } else {
                        if (count != 0) {
                            String substr = s.substring(s.length() - 2);
                            if (substr.contains(".") || substr.contains(",")) {
                                s += "0";
                            }//end if
                        }//end if
                    }//end else

                    editText.removeTextChangedListener(discount);
                    String cleanString = s.toString().replaceAll("[,.]", "");

                    BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    DecimalFormat df = new DecimalFormat("0.00");
                    String formatted = df.format(parsed);
                    current = formatted;
                    editText.setText(formatted);
                    if(formatted.length() > 5){
                        editText.setSelection(5);
                    }else {
                        editText.setSelection(formatted.length());
                    }
                    editText.addTextChangedListener(discount);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }//end catch
            }//end if
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

}
