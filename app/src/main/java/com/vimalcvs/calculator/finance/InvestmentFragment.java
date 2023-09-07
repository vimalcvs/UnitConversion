package com.vimalcvs.calculator.finance;

import static com.vimalcvs.calculator.utils.Utils.closeKeyboard;

import android.annotation.SuppressLint;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.vimalcvs.calculator.R;
import com.vimalcvs.calculator.utils.Utils;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class InvestmentFragment extends Fragment {
    private TextInputEditText investmentAmountEditText, settlementAmountEditText;
    private TextView profitTextView;
    private TextView profitMarginTextView;
    private TextView roiTextView;
    private Button btnStartDate;
    private Button btnEndDate;

    private Date chosenTimeStart = new Date();
    private Date chosenTimeEnd = new Date();
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
                calculateROI();
            } catch (Exception ignored) {

            }
        }
    };

    public InvestmentFragment() {
        // Required empty public constructor
    }

    public static InvestmentFragment newInstance() {
        return new InvestmentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_investment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        investmentAmountEditText = view.findViewById(R.id.investmentAmountEditText);
        settlementAmountEditText = view.findViewById(R.id.settlementAmountEditText);
        settlementAmountEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(getActivity());
                settlementAmountEditText.clearFocus();
                return true;
            }
            return false;
        });
        investmentAmountEditText.addTextChangedListener(textWatcher);
        settlementAmountEditText.addTextChangedListener(textWatcher);

        Calendar calendar = Calendar.getInstance();

        // Initialize the MaterialDatePicker for the start date
        MaterialDatePicker.Builder<Long> builderStart = MaterialDatePicker.Builder.datePicker();
        builderStart.setTitleText("");
        builderStart.setSelection(calendar.getTimeInMillis());
        MaterialDatePicker<Long> materialDatePickerStart = builderStart.build();

        // Set the callback for when a start date is selected
        materialDatePickerStart.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);
            chosenTimeStart = selectedDate.getTime();
            updateTimeStart();
        });

        // Initialize the MaterialDatePicker for the end date
        MaterialDatePicker.Builder<Long> builderEnd = MaterialDatePicker.Builder.datePicker();
        builderEnd.setTitleText("");
        builderEnd.setSelection(calendar.getTimeInMillis());
        MaterialDatePicker<Long> materialDatePickerEnd = builderEnd.build();

        // Set the callback for when an end date is selected
        materialDatePickerEnd.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);
            chosenTimeEnd = selectedDate.getTime();
            updateTimeEnd();
        });

        btnStartDate = view.findViewById(R.id.btn_start_date);
        btnEndDate = view.findViewById(R.id.btn_end_date);
        btnStartDate.addTextChangedListener(textWatcher);
        btnEndDate.addTextChangedListener(textWatcher);

        // Show the dialog when the start date button is clicked
        btnStartDate.setOnClickListener(v -> materialDatePickerStart.show(getParentFragmentManager(), "DATE_PICKER_START"));
        // Show the dialog when the end date button is clicked
        btnEndDate.setOnClickListener(v -> materialDatePickerEnd.show(getParentFragmentManager(), "DATE_PICKER_END"));

        updateTimeStart();
        updateTimeEnd();
        profitTextView = view.findViewById(R.id.profitTextView);
        profitMarginTextView = view.findViewById(R.id.profitMarginTextView);
        roiTextView = view.findViewById(R.id.roiTextView);

        view.findViewById(R.id.info).setOnClickListener(v -> Snackbar
                .make(view, getString(R.string.tip), Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show());
    }

    @SuppressLint("SetTextI18n")
    private void calculateROI() {
        double investmentAmount = Double.parseDouble(investmentAmountEditText.getText().toString());
        double settlementAmount = Double.parseDouble(settlementAmountEditText.getText().toString());

        Date chosenTimeStartS;
        Date chosenTimeEndS;

        if (chosenTimeStart.compareTo(chosenTimeEnd) == 0) {
            return;
        }
        if (chosenTimeStart.after(chosenTimeEnd)) {
            chosenTimeStartS = chosenTimeEnd;
            chosenTimeEndS = chosenTimeStart;
        } else {
            chosenTimeStartS = chosenTimeStart;
            chosenTimeEndS = chosenTimeEnd;
        }
        try {
            long durationInMillis = chosenTimeEndS.getTime() - chosenTimeStartS.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(durationInMillis);
            double profit = settlementAmount - investmentAmount;
            double profitMargin = (profit / investmentAmount) * 100;
            double roi = (Math.pow(settlementAmount / investmentAmount, 365.0 / days) - 1) * 100;
            //double roi = profit / investmentAmount / days * 365;

            profitTextView.setText(getString(R.string.profit) + " " + Utils.formatNumberFinance(String.valueOf(profit)));
            profitMarginTextView.setText(getString(R.string.profitMargin) + " " + String.format(Locale.getDefault(), "%.2f%%", profitMargin));
            roiTextView.setText(getString(R.string.annualized_rate_of_return) + " " + String.format(Locale.getDefault(), "%.2f%%", roi));
        } catch (Exception ignored) {
        }
    }

    private void updateTimeStart() {
        btnStartDate.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(chosenTimeStart));
    }

    private void updateTimeEnd() {
        btnEndDate.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(chosenTimeEnd));
    }
}
