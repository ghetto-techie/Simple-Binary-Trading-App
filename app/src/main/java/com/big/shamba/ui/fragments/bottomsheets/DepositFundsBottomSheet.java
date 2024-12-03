package com.big.shamba.ui.fragments.bottomsheets;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.big.shamba.R;
import com.big.shamba.data.daraja.MpesaService;
import com.big.shamba.models.dto.OAuthResponse;
import com.big.shamba.models.dto.STKPushRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositFundsBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "DepositFundsBottomSheet";
    private TextInputLayout depositAmountTIL;
    private TextInputLayout mpesaPhoneNumberTIL;
    private MaterialButton depositButton;
    ProgressDialog progressDialog;
    private MpesaService mpesaService;
    private String consumerKey = "a5OZxKJwFMTBxEUjYXiY7LOWrc7u1wfHGuvnbKOH4SpR8oM2";
    private String consumerSecret = "vsCFsiA441ZbpaGRYKWk3sYElpI5h2b78OnBHCGCU8CRmrQZRwbGtpMFA8AX8YRF";


    public DepositFundsBottomSheet() {
        Log.d(TAG, "DepositFundsBottomSheet: Launched...");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_deposit, container, false);
        progressDialog = new ProgressDialog(requireContext());
        depositAmountTIL = view.findViewById(R.id.depositAmountTIL);
        mpesaPhoneNumberTIL = view.findViewById(R.id.mpesaPhoneNumber);
        depositButton = view.findViewById(R.id.depositFundsBottomSheetButton);

        return view;
    }

    private boolean isValidKenyanPhoneNumber(String phoneNumber) {
        // Define the regex pattern for Kenyan phone numbers
        String regex = "^((\\+254)|254|0)?7[0-9]{8}$|^((\\+254)|254|0)?1[0-9]{8}$";

        // Compile the regex
        Pattern pattern = Pattern.compile(regex);

        // Match the phone number with the regex pattern
        Matcher matcher = pattern.matcher(phoneNumber);

        // Return whether the phone number matches the pattern
        return matcher.matches();
    }


    private void performSTKPush(Response<OAuthResponse> response, long amountInKsh, String mpesaPhoneNumber) {
        String accessToken = response.body().getAccess_token();

        STKPushRequest stkPushRequest = new STKPushRequest(
                "174379", // Business short code
                "passkey", // Passkey
                String.valueOf(amountInKsh), // Amount
                mpesaPhoneNumber, // Phone number
                "174379", // Party A
                "254707670113", // Party B
                "https://example.com/callback", // Callback URL
                "Account Reference",
                "Transaction Description"
        );

//        mpesaService.performSTKPush(
//                accessToken,
//                stkPushRequest,
//                new Callback<STKPushResponse>() {
//                    @Override
//                    public void onResponse(Call<STKPushResponse> call, Response<STKPushResponse> response) {
//                        progressDialog.dismiss();
//                        if (response.isSuccessful()) {
//                            STKPushResponse stkPushResponse = response.body();
//                            Log.d(TAG, "onResponse: STKPush Successful -> Deposit for " + stkPushRequest.toString() + " Successful : " + stkPushResponse);
////                            Toast.makeText(requireActivity(), response.body() + "\n" + response.message(), Toast.LENGTH_SHORT).show();
//                            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                            Transaction.MpesaTransaction mpesaTransaction = new Transaction.MpesaTransaction(
//                                    currentUserId,
//                                    amountInKsh,
//                                    String.valueOf(System.currentTimeMillis()),
//                                    TransactionPlatform.MPESA,
//                                    TransactionType.DEPOSIT,
//                                    stkPushResponse
//                            );
//
//                            updateTransactionHistoryOnDatabase(
//                                    mpesaTransaction
//                            );
//                        } else {
//                            Log.d(TAG, "onResponse: STKPush Failed -> Failed to make deposit: " + response.message());
//                        }
//                        depositButton.setEnabled(true);
//                    }
//
//                    @Override
//                    public void onFailure(Call<STKPushResponse> call, Throwable t) {
//                        progressDialog.dismiss();
//                        depositButton.setEnabled(true);
//                        t.printStackTrace();
//                        Log.d(TAG, "onFailure: STKPush Failed -> Failed to perform STK Push: " + t.getMessage().toString());
//                    }
//                })
//        ;

    }

//    private void updateTransactionHistoryOnDatabase(Transaction.MpesaTransaction mpesaTransaction) {
//        FirebaseFirestore.getInstance()
//                .collection("transactions")
//                .add(mpesaTransaction)
//                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(requireContext(), "Deposit Successful", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "onComplete: Transaction recorded successfully -> " + task.getResult().toString());
//                        } else {
//                            Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "onComplete: Transaction record failed!! -> " + task.getException().getMessage().toString());
//                        }
//                    }
//                })
//        ;
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        depositButton
                .setOnClickListener(v -> {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        depositButton.setEnabled(false);
                        initiateMpesa();
                    } else {
                        Toast.makeText(requireActivity(), "Please log in to make a deposit.", Toast.LENGTH_SHORT).show();
                    }
                })
        ;
    }

    private void initiateMpesa() {
        progressDialog.setTitle("Depositing funds");
        progressDialog.setMessage("Processing transaction...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mpesaService = new MpesaService();

        String amountInKsh = depositAmountTIL.getEditText().getText().toString();
        String mpesaPhoneNumber = mpesaPhoneNumberTIL.getEditText().getText().toString();

        if (amountInKsh.isEmpty()) {
            depositAmountTIL.requestFocus();
            depositAmountTIL.setError("Amount from KES. 100/=");
            progressDialog.dismiss();
            depositButton.setEnabled(true);
        } else {
            depositAmountTIL.setError(null);
            if (!isValidKenyanPhoneNumber(mpesaPhoneNumber)) {
                mpesaPhoneNumberTIL.requestFocus();
                mpesaPhoneNumberTIL.setError("Enter a valid mpesa phone number. For example, 25470****20");
                progressDialog.dismiss();
                depositButton.setEnabled(true);
            } else {
                mpesaPhoneNumberTIL.setError(null);
                mpesaService
                        .getOAuthToken(
                                consumerKey,
                                consumerSecret,
                                new Callback<OAuthResponse>() {
                                    @Override
                                    public void onResponse(Call<OAuthResponse> call, Response<OAuthResponse> response) {
                                        if (response.isSuccessful()) {
                                            performSTKPush(response, Long.parseLong(amountInKsh), mpesaPhoneNumber);
                                            Log.d(TAG, "Token obtained: " + response.body().getAccess_token());
                                        } else {
                                            Toast.makeText(requireContext(), "Soemthing went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onResponse: " + response.message() + "\n" + response.errorBody().toString());

                                            progressDialog.dismiss();
                                            depositButton.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<OAuthResponse> call, Throwable t) {
                                        progressDialog.dismiss();
                                        Log.d(TAG, "onFailure: Failed to get token: " + t.getLocalizedMessage());
                                        t.printStackTrace();
                                        depositButton.setEnabled(true);
                                    }
                                })
                ;
            }
        }


    }
}
