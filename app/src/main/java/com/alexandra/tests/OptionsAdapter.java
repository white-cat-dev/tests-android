package com.alexandra.tests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class OptionsAdapter extends BaseAdapter {

    ArrayList<QuestionOption> options;
    LayoutInflater lInflater;
    TestActivity context;
    boolean testFinished;

    int selectedOption;

    OptionsAdapter(TestActivity context, ArrayList<QuestionOption> options, boolean testFinished, int selectedOption) {
        this.context = context;
        this.options = options;
        this.testFinished = testFinished;
        this.selectedOption = selectedOption;

        lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = lInflater.inflate(R.layout.option_item, parent, false);
        }

        QuestionOption option = (QuestionOption)getItem(position);

        View wordingView = itemView.findViewById(R.id.wording);

        ((TextView)wordingView).setText(option.wording);

        if (testFinished) {
            if (option.isRight) {
                wordingView.setBackgroundResource(R.drawable.input_answer);
            }
            if ((option.id == selectedOption) && (option.isRight)) {
                wordingView.setBackgroundResource(R.drawable.input_right);
            }
            else if ((option.id == selectedOption) && (!option.isRight)) {
                wordingView.setBackgroundResource(R.drawable.input_wrong);
            }
        }
        else {
            if (option.id == selectedOption) {
                wordingView.setBackgroundResource(R.drawable.input_active);
            } else {
                wordingView.setBackgroundResource(R.drawable.input);
            }

            wordingView.setOnClickListener(view -> {
                selectedOption = option.id;
                context.selectQuestionOption(option.id);
                notifyDataSetChanged();
            });
        }

        return itemView;
    }
}