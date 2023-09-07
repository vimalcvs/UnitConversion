package com.vimalcvs.calculator.finance;

import static com.vimalcvs.calculator.utils.Utils.closeKeyboard;
import static com.vimalcvs.calculator.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputEditText;
import com.vimalcvs.calculator.R;
import com.vimalcvs.calculator.databinding.FragmentVatBinding;

import java.util.ArrayList;


public class VatFragment extends Fragment {
    FragmentVatBinding binding;
    private TextInputEditText editTextAmount, editTextVatRate;
    private CheckBox checkBoxVatIncluded;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                calculateVAT();
            } catch (Exception ignored) {

            }
        }
    };

    public VatFragment() {
        // Required empty public constructor
    }

    public static VatFragment newInstance() {
        return new VatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextVatRate = view.findViewById(R.id.editTextVatRate);
        editTextAmount.addTextChangedListener(textWatcher);
        editTextVatRate.addTextChangedListener(textWatcher);
        editTextVatRate.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(getActivity());
                editTextVatRate.clearFocus();
                return true;
            }
            return false;
        });
        checkBoxVatIncluded = view.findViewById(R.id.checkBoxVatIncluded);
        checkBoxVatIncluded.setOnCheckedChangeListener((compoundButton, b) -> calculateVAT());
    }

    @SuppressLint("SetTextI18n")
    private void calculateVAT() {
        double amount = Double.parseDouble(editTextAmount.getText().toString());
        double vatRate = Double.parseDouble(editTextVatRate.getText().toString());
        boolean vatIncluded = checkBoxVatIncluded.isChecked();

        double vatAmount;
        double totalAmount;
        double vatDeductedAmount;
        if (vatIncluded) {
            vatDeductedAmount = amount / (1 + vatRate) * vatRate;
            totalAmount = amount;
            vatAmount = amount - vatDeductedAmount;
        } else {
            vatAmount = amount * vatRate / 100;
            totalAmount = amount + vatAmount;
            vatDeductedAmount = amount;
        }

        // 将数据转换为float类型
        float vatValue = Float.parseFloat(String.valueOf(vatAmount));
        float vatDeductedValue = Float.parseFloat(String.valueOf(vatDeductedAmount));
        // 创建饼状图数据项
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(vatValue, getString(R.string.vat)));
        entries.add(new PieEntry(vatDeductedValue, getString(R.string.taxExcludedAmount)));
        // 创建饼状图数据集
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // 创建饼状图数据对象
        PieData data = new PieData(dataSet);
        data.setValueTextSize(26);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(formatNumberFinance(String.valueOf(value)));
            }
        });
        // 获取饼状图视图
        PieChart pieChart = binding.pieChart;
        pieChart.setVisibility(View.VISIBLE);
        // 设置数据
        pieChart.setData(data);
        pieChart.setCenterText(getString(R.string.taxIncludedAmount) + " " + formatNumberFinance(String.valueOf(totalAmount)));
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        // 刷新图表
        pieChart.invalidate();
    }
}
