package com.incomecalculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.incomecalculator.shifts.Shift;

/**
 * An adapter for displaying shift information in a RecyclerView.
 */
public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ViewHolder> {

    private Shift[] shifts;

    /**
     * ViewHolder for the String representation of a shift.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        //--- Constructor ---//

        public ViewHolder(View view) {

            super(view);

            textView = view.findViewById(R.id.list_item_text);
        }

        //--- Getter ---//

        public TextView getTextView() {
            return textView;
        }
    }

    //--- Constructor ---//

    public ShiftAdapter(Shift[] shifts) {
        this.shifts = shifts;
    }

    //--- Event Listeners ---//

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(shifts[position].toString());
    }

    //--- Getters ---//

    @Override
    public int getItemCount() {
        return (shifts == null) ? 0 : shifts.length;
    }

}
