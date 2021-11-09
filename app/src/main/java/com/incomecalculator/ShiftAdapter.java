package com.incomecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.incomecalculator.shifts.Shift;

import java.util.ArrayList;

/**
 * An adapter for displaying shift information in a RecyclerView.
 */
public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ViewHolder> {

    private SQLiteDatabase db;
    private ArrayList<Shift> shifts;

    /**
     * ViewHolder for the String representation of a shift.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final TextView textView;
        private final Button editButton;
        private final Button deleteButton;

        //--- Constructor ---//

        public ViewHolder(View view) {

            super(view);

            this.view = view;
            textView = view.findViewById(R.id.list_item_text);
            editButton = view.findViewById(R.id.edit_shift_button);
            deleteButton = view.findViewById(R.id.delete_shift_button);
        }

        //--- Getters ---//

        public View getView() { return view; }

        public TextView getTextView() {
            return textView;
        }

        public Button getEditButton() { return editButton; }

        public Button getDeleteButton() { return deleteButton; }
    }

    //--- Constructor ---//

    public ShiftAdapter(ArrayList<Shift> shifts, SQLiteDatabase db) {

        this.shifts = shifts;
        this.db = db;
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

        holder.getTextView().setText(shifts.get(position).toString());

        holder.getDeleteButton().setOnClickListener(
                (view) -> confirmShiftDeletion(holder.getView(), position));

        holder.getEditButton().setOnClickListener(
                (view) -> launchEditShiftActivity(holder.getView(), position));
    }

    //--- Getters ---//

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    //--- Helper Methods ---//

    public void launchEditShiftActivity(View view, int position) {

        Intent intent = new Intent(view.getContext(), ModifyShiftActivity.class);
        intent.putExtra("type", ModifyShiftActivity.Type.EDIT);
        intent.putExtra("shiftID", shifts.get(position).getID());
        view.getContext().startActivity(intent);
    }

    /**
     * Deletes the shift at the given position from the database and the
     * displayed list of shifts if the user confirms its deletion.
     */
    private void confirmShiftDeletion(View view, int position) {

        // Create a confirmation dialog for deleting the shift
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
        dialogBuilder.setMessage("Delete shift?").setTitle("Confirm Shift Deletion");

        // Delete the shift if this option is selected
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteShift(position);
            }
        });

        // Return to the list of shifts if this option is selected
        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        dialogBuilder.show();
    }

    private void deleteShift(int position) {

        shifts.get(position).deleteFromDatabase(db);
        shifts.remove(position);
        notifyDataSetChanged();
    }

}
