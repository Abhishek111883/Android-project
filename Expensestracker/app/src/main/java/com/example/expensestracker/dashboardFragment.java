package com.example.expensestracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensestracker.Model.data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import com.example.expensestracker.Numberutills;



public class dashboardFragment extends Fragment {

private FloatingActionButton fab_main_btn;
private FloatingActionButton fab_income_btn;

private FloatingActionButton fab_expense_btn;


//Floating text view
private TextView fab_income_txt;
private TextView fab_expense_txt;

//text view

    private TextView incmoeinmain;

    private TextView expenseinmain;

    private TextView balncetxt;


private boolean isopen = false;


//Animation
    private Animation fadopen,fadclose;


    ///firebase..
    private FirebaseAuth mAuth;
    private DatabaseReference incomedatabase;

    private DatabaseReference expensedatabase;


    //backbutton

    private static final int BACK_BUTTON_INTERVAL = 2000; // Define the time interval in milliseconds
    private long backButtonPressedTime = 0;


    // recycler view in dashboard

    private RecyclerView incomerecycler;

    private RecyclerView expenserecycler;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blank, container, false);

        //
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser muser  = mAuth.getCurrentUser();
        assert muser != null;
        String uid = muser.getUid();



        incomedatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        expensedatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        incomedatabase.keepSynced(true);
        expensedatabase.keepSynced(true);

        incmoeinmain = view.findViewById(R.id.income_at_set);

        expenseinmain = view.findViewById(R.id.expese_at_set);

        balncetxt = view.findViewById(R.id.balanceamount);

        //recyclers

        incomerecycler = view.findViewById(R.id.recyclerview_in_dashboard_for_income);
        expenserecycler =  view.findViewById(R.id.recyclerview_in_dashboard_for_expense);





        //connect floating button to layout

        fab_main_btn = view.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = view.findViewById(R.id.income_ft_btn);
        fab_expense_btn = view.findViewById(R.id.expense_ft_btn);

        fab_income_txt = view.findViewById(R.id.income_ft_text);
        fab_expense_txt = view.findViewById(R.id.expense_ft_text);


        fadopen = AnimationUtils.loadAnimation(getActivity(),R.anim.flotingactionbaropen);
        fadclose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);





        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftanimation();

                adddata();


            }
        });

/// for main income
        incomedatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double totalincome = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    data dat = mysnap.getValue(data.class);

                    assert dat != null;
                    totalincome += dat.getAmount();
                }

                String sttotalvalue = Numberutills.formatNumber(totalincome);


                incmoeinmain.setText(sttotalvalue);
                updateBalance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //for expense

        expensedatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double totalexpense = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    data dat = mysnap.getValue(data.class);

                    assert dat != null;
                    totalexpense += dat.getAmount();
                }
//                String sttotalvalue = String.valueOf(totalexpense);


                String sttotalvalue = Numberutills.formatNumber(totalexpense);

                expenseinmain.setText(sttotalvalue);

                // Calculate balance after updating expenses
                updateBalance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








        LinearLayoutManager layoutManagerincome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerincome.setReverseLayout(true);
        layoutManagerincome.setStackFromEnd(true);
        incomerecycler.setHasFixedSize(true);
        HorizontalSpaceItemDecoration itemDecoration = new HorizontalSpaceItemDecoration(10);
        incomerecycler.addItemDecoration(itemDecoration);
        incomerecycler.setLayoutManager(layoutManagerincome);


        LinearLayoutManager layoutManagerexpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerexpense.setReverseLayout(true);
        layoutManagerexpense.setStackFromEnd(true);
        expenserecycler.setHasFixedSize(true);
        HorizontalSpaceItemDecoration itemDecoration2 = new HorizontalSpaceItemDecoration(10);
        expenserecycler.addItemDecoration(itemDecoration2);
        expenserecycler.setLayoutManager(layoutManagerexpense);




        return view;
    }


    // Helper method to update the balance
    private void updateBalance() {
        String incomeText = incmoeinmain.getText().toString().replace(",", "");
        String expenseText = expenseinmain.getText().toString().replace(",", "");

        if (!TextUtils.isEmpty(incomeText) && !TextUtils.isEmpty(expenseText)) {
            double TotalIncome = Double.parseDouble(incomeText);
            double TotalExpense = Double.parseDouble(expenseText);

            double balance = TotalIncome - TotalExpense;

            // Format the balance using a DecimalFormat
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String formattedBalance = decimalFormat.format(balance);

            balncetxt.setText(formattedBalance);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        backButtonPressedTime = 0; // Reset the back button timer when the fragment is resumed.
    }


    public void onBackPressed() {
        // Check if the back button is pressed within the specified interval
        if (backButtonPressedTime + BACK_BUTTON_INTERVAL > System.currentTimeMillis()) {
            // If pressed twice within the interval, exit the app
            getActivity().finishAffinity();
        } else {
            Toast.makeText(getActivity(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            backButtonPressedTime = System.currentTimeMillis();

        }
    }

    private void ftanimation(){
        if (isopen){
            fab_income_btn.startAnimation(fadclose);
            fab_expense_btn.startAnimation(fadclose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(fadclose);
            fab_expense_txt.startAnimation(fadclose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);

            isopen = false;

        }

        else {
            fab_income_btn.startAnimation(fadopen);
            fab_expense_btn.startAnimation(fadopen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fadopen);
            fab_expense_txt.startAnimation(fadopen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);

            isopen = true;

        }
    }

    private void adddata(){

        // adding data
            fab_income_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incomedataINsert();
                }
            });

            fab_expense_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expensedatinsert();
                }
            });
    }

    private void expensedatinsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_adding_data,null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        EditText editamount = myview.findViewById(R.id.Amount_edit);
        EditText editpurpose = myview.findViewById(R.id.purpose_edit);
        EditText editnote = myview.findViewById(R.id.Note_edit);

        Button btnsave = myview.findViewById(R.id.btn_save);
        Button btncancel = myview.findViewById(R.id.btn_cancel);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String purpose = editpurpose.getText().toString().trim();
                String amount = editamount.getText().toString().trim();

                String note = editnote.getText().toString().trim();


                if (TextUtils.isEmpty(amount)){
                    editamount.setError("Required....");
                    return;
                }

                int ourammountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(purpose)){
                    editpurpose.setError("Required....");
                    return;
                }
                if (TextUtils.isEmpty(note)){
                    editnote.setError("Required....");
                    return;
                }

                String id = expensedatabase.push().getKey();

                String mdate = DateFormat.getInstance().format(new Date());

                data data = new data(ourammountint,purpose,note,id,mdate);

                assert id != null;
                expensedatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Inserted", Toast.LENGTH_SHORT).show();
                ftanimation();

                dialog.dismiss();


            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftanimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    public  void incomedataINsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_adding_data,null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        EditText editamount = myview.findViewById(R.id.Amount_edit);
        EditText editpurpose = myview.findViewById(R.id.purpose_edit);
        EditText editnote = myview.findViewById(R.id.Note_edit);

        Button btnsave = myview.findViewById(R.id.btn_save);
        Button btncancel = myview.findViewById(R.id.btn_cancel);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String purpose = editpurpose.getText().toString().trim();
                String amount = editamount.getText().toString().trim();

                String note = editnote.getText().toString().trim();


                if (TextUtils.isEmpty(amount)){
                    editamount.setError("Required....");
                    return;
                }

                int ourammountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(purpose)){
                    editpurpose.setError("Required....");
                    return;
                }
                if (TextUtils.isEmpty(note)){
                    editnote.setError("Required....");
                    return;
                }

                String id = incomedatabase.push().getKey();

                String mdate = DateFormat.getInstance().format(new Date());

                data data = new data(ourammountint,purpose,note,id,mdate);

                assert id != null;
                incomedatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Inserted", Toast.LENGTH_SHORT).show();

                ftanimation();
                dialog.dismiss();


            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftanimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    //for income recycler in dashboard

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<data> option = new FirebaseRecyclerOptions.Builder<data>()
                .setQuery(incomedatabase, data.class)
                .build();

        FirebaseRecyclerAdapter<data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<data, IncomeViewHolder>(option) {
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.incomedashboardrecycler,parent,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull data model) {
                holder.setIncomePurpose(model.getPurpose());
                holder.setIncomeAmount(model.getAmount());

                holder.setIncomedate(model.getData());
            }
        };
        incomerecycler.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        FirebaseRecyclerOptions<data> option1 = new FirebaseRecyclerOptions.Builder<data>()
                .setQuery(expensedatabase, data.class)
                .build();

        FirebaseRecyclerAdapter<data, ExpenseViewHolder> ExpenseAdapter = new FirebaseRecyclerAdapter<data, ExpenseViewHolder>(option1) {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expensedashboardrecycler,parent,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull data model) {
                holder.setExpensePurpose(model.getPurpose());
                holder.setExpenseAmount(model.getAmount());
                holder.setExpensedate(model.getData());
            }
        };
        expenserecycler.setAdapter(ExpenseAdapter);
        ExpenseAdapter.startListening();
    }
    //for income Data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mincomeview;

        public IncomeViewHolder(View itemView){
            super(itemView);
            mincomeview = itemView;
        }

        public void setIncomePurpose(String purpose){
            TextView mpurpose = mincomeview.findViewById(R.id.purpose_in_dashboard);
            mpurpose.setText(purpose);
        }

        public void setIncomeAmount(int amount){
            TextView mAmount = mincomeview.findViewById(R.id.dashboard_recycle_income);
            String formattedAmount = Numberutills.formatNumber(amount);
            mAmount.setText(formattedAmount);
        }

        public void setIncomedate(String date){
            TextView mDate = mincomeview.findViewById(R.id.dashboard_recycle_date);
            mDate.setText(date);
        }

    }


    // for expense recycler in dashboard



    //for income Data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mexpenseview;

        public ExpenseViewHolder(View itemView){
            super(itemView);
            mexpenseview = itemView;
        }

        public void setExpensePurpose(String purpose){
            TextView mpurpose = mexpenseview.findViewById(R.id.purpose_in_dashboard_expense);
            mpurpose.setText(purpose);
        }

        public void setExpenseAmount(int amount){
            TextView mAmount = mexpenseview.findViewById(R.id.dashboard_recycle_expense);

            String formattedAmount = Numberutills.formatNumber(amount);
            mAmount.setText(formattedAmount);
        }

        public void setExpensedate(String date){
            TextView mDate = mexpenseview.findViewById(R.id.dashboard_recycle_date_expense);
            mDate.setText(date);
        }

    }




}