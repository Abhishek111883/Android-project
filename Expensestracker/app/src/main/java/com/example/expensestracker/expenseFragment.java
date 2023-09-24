package com.example.expensestracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.Model.data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.NumberUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class expenseFragment extends Fragment {

    private DatabaseReference mexpensedatabase;
    //Recycler vieww

    private RecyclerView recycleview;
    
    private TextView totalexpensetext;

    //update edit text

    private EditText edtamount;
    private EditText edtpurpose;
    private EditText edtnote;

    //update btns

    private Button btn_update;
    private Button btn_delete;

    // data item values

    private String purpose;
    private String note;
    private int amount;

    private String post_key;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_expense, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser muser = mAuth.getCurrentUser();

        assert muser != null;
        String uid = muser.getUid();

        mexpensedatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        recycleview = myview.findViewById(R.id.recyclerview_in_expense);
        
        totalexpensetext = myview.findViewById(R.id.Expense_in_Expensefragment);
        

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycleview.setHasFixedSize(true);
        verticalSpacingitemDecoration itemDecoration = new verticalSpacingitemDecoration(10);
        recycleview.addItemDecoration(itemDecoration);
        recycleview.setLayoutManager(layoutManager);


        ValueEventListener valueEventListener = mexpensedatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double totalexpense = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    data dat = mysnap.getValue(data.class);

                    totalexpense += dat.getAmount();

//                    String sttotalvalue = String.valueOf(totalexpense);

                   
                    String sttotalvalue = Numberutills.formatNumber(totalexpense);

                    totalexpensetext.setText(sttotalvalue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myview;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<data> option = new FirebaseRecyclerOptions.Builder<data>()
                .setQuery(mexpensedatabase,data.class)
                .build();


        FirebaseRecyclerAdapter<data, MyviewHolder> adapter = new FirebaseRecyclerAdapter<data, MyviewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull MyviewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getPurpose());
                holder.setNote(model.getNote());
                holder.setDate(model.getData());


                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        amount = model.getAmount();
                        purpose = model.getPurpose();
                        note = model.getNote();

                        post_key = getRef(position).getKey(); // Set post_key here


                        updateitem();
                    }
                });
            }

            @NonNull
            @Override
            public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false));
            }
        };
        recycleview.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MyviewHolder extends RecyclerView.ViewHolder{

        View myview;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }
        public void setType(String type){
            TextView mytype = myview.findViewById(R.id.Type_txt_in_Expense);
            mytype.setText(type);
        }

        public void setNote(String note){
            TextView mynote = myview.findViewById(R.id.Note_txt_in_Expense);
            mynote.setText(note);
        }

        public void setDate(String date){
            TextView mydate = myview.findViewById(R.id.date_txt_in_Expense);
            mydate.setText(date);
        }

        private void setAmount(int amount){
            TextView amountexpense = myview.findViewById(R.id.expense_txt_in_Expense);
//            String stamount = "-" + String.valueOf(amount);

            String stamount = "-" + Numberutills.formatNumber(amount);
            amountexpense.setText(stamount);
        }




    }


    private void updateitem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_item,null);
        mydialog.setView(myview);

        edtamount = myview.findViewById(R.id.Amount_edit);
        edtpurpose = myview.findViewById(R.id.purpose_edit);
        edtnote = myview.findViewById(R.id.Note_edit);

        //set data to edt text;

        edtamount.setText(String.valueOf(amount));
        edtamount.setSelection(String.valueOf(amount).length());

        edtpurpose.setText(purpose);
        edtpurpose.setSelection(purpose.length());

        edtnote.setText(note);
        edtnote.setSelection(note.length());

        btn_update = myview.findViewById(R.id.btn_update);
        btn_delete = myview.findViewById(R.id.btn_delete);


        AlertDialog dialog = mydialog.create();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String mamount = String.valueOf(amount);
                mamount = edtamount.getText().toString().trim();

                purpose = edtpurpose.getText().toString().trim();

                note = edtnote.getText().toString().trim();



                int myAmount = Integer.parseInt(mamount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                data dat = new data(myAmount,purpose,note,post_key,mDate);

                mexpensedatabase.child(post_key).setValue(dat);

                dialog.dismiss();

                Toast.makeText(getActivity(), "DATA UPDATED", Toast.LENGTH_SHORT).show();


            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mexpensedatabase.child(post_key).removeValue();

                dialog.dismiss();
            }

        });
        dialog.show();

    }



}