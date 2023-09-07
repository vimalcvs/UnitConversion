package com.vimalcvs.calculator.exchange;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


public class CurrencyAdapter extends ArrayAdapter<Currency> {

    public CurrencyAdapter(Context context, List<Currency> currencies) {
        super(context, 0, currencies);
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Currency currency = getItem(position);

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(currency.getSymbol() + " - " + currency.getChineseName() + " - " + currency.getEnglishName());

        return convertView;
    }
}
