package com.example.projectgroup1;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import model.Account;

public class WelcomeActivity extends AppCompatActivity {
    Account loggedInAccount;
    MediaPlayer mediaPlayer;
    private int selectedBalance = 0, musicPlayPosition, totalBetAmount;
    int blackCarBet = 0, blueCarBet = 0, greenCarBet = 0, purpleCarBet = 0;
    Map<ImageView, Long> finishingTimes = new HashMap<>();
    Button showBalance, playButton, resetButton, startButton;
    ObjectAnimator carAnimator;
    ImageView blackCar, blueCar, greenCar, purpleCar;
    CheckBox blackCarCheckBox, blueCarCheckBox, greenCarCheckBox, purpleCarCheckBox;
    List<CheckBox> carBetPlace = new ArrayList<>();
    EditText blackCarBetAmount, blueCarBetAmount, greenCarBetAmount, purpleCarBetAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        playButton = findViewById(R.id.playBtn);
        resetButton = findViewById(R.id.resetBtn);
        Button logoutButton = findViewById(R.id.buttonLogout);
        Button controlMusic = findViewById(R.id.buttonControlMusic);
        blackCar = findViewById(R.id.imageView4);
        blueCar = findViewById(R.id.imageView5);
        greenCar = findViewById(R.id.imageView6);
        purpleCar = findViewById(R.id.imageView7);

        showBalance = findViewById(R.id.showBalanceBtn);
        TextView welcomeMessage = findViewById(R.id.textView);
        loggedInAccount = (Account) getIntent().getSerializableExtra("loggedInAccount");
        welcomeMessage.setText("Welcome, " + loggedInAccount.getUsername() + "!");
        showBalance.setText("Balance: " + loggedInAccount.getBalance());
        mediaPlayer = MediaPlayer.create(this, R.raw.welcome_music);
        if (!mediaPlayer.isPlaying())
        {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        // Logout
        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            startActivity(intent);
        });

        // Pause music
        controlMusic.setOnClickListener(view -> {
            String musicStatus = (String) controlMusic.getText();
            switch (musicStatus) {
                case "Pause music" :
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        musicPlayPosition = mediaPlayer.getCurrentPosition();
                    }
                    controlMusic.setText("Play music");
                    break;
                case "Play music":
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(musicPlayPosition);
                        mediaPlayer.start();
                    }
                    controlMusic.setText("Pause music");
                    break;
            }
        });

        //Add balance
        showBalance.setOnClickListener(view -> {
            showBalancePopup();
        });

        playButton.setOnClickListener(view -> {
            showBetPopup();
        });

        //Reset car position
        resetButton.setOnClickListener(view -> {
            // If user press start while the race is staring
            if (finishingTimes.size() < 4) {
                Toast.makeText(WelcomeActivity.this, "Please wait to the race end to reset", Toast.LENGTH_SHORT).show();
                return;
            }

            blackCar.setY(1200);
            blueCar.setY(1200);
            greenCar.setY(1200);
            purpleCar.setY(1200);
            finishingTimes.clear(); // Clear the finishingTimes Map when race finished
            blackCarBet = 0; // Reset the bet money to 0 for all cars
            blueCarBet = 0;
            greenCarBet = 0;
            purpleCarBet = 0;
            carBetPlace.clear(); // Clear all bet checkbox of the previous race
        });
    }

    private void showBetPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bet_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
        startButton = dialogView.findViewById(R.id.buttonStart);
        blackCarCheckBox = dialogView.findViewById(R.id.checkbox_blackcar);
        blueCarCheckBox = dialogView.findViewById(R.id.checkbox_bluecar);
        greenCarCheckBox = dialogView.findViewById(R.id.checkbox_greencar);
        purpleCarCheckBox = dialogView.findViewById(R.id.checkbox_purplecar);
        blackCarBetAmount = dialogView.findViewById(R.id.edittext_blackcar_bet);
        blueCarBetAmount = dialogView.findViewById(R.id.edittext_bluecar_bet);
        greenCarBetAmount = dialogView.findViewById(R.id.edittext_greencar_bet);
        purpleCarBetAmount = dialogView.findViewById(R.id.edittext_purplecar_bet);

        //Enable edittext when user check on checkbox
        blackCarCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            blackCarBetAmount.setEnabled(isChecked);
            if (isChecked) {
                carBetPlace.add(blackCarCheckBox);
            } else
                carBetPlace.remove(blackCarCheckBox);
        });

        blueCarCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            blueCarBetAmount.setEnabled(isChecked);
            if (isChecked) {
                carBetPlace.add(blueCarCheckBox);
            } else carBetPlace.remove(blueCarCheckBox);
        });

        greenCarCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            greenCarBetAmount.setEnabled(isChecked);
            if (isChecked) {
                carBetPlace.add(greenCarCheckBox);
            } else carBetPlace.remove(greenCarCheckBox);
        });

        purpleCarCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            purpleCarBetAmount.setEnabled(isChecked);
            if (isChecked) {
                carBetPlace.add(purpleCarCheckBox);
            } else carBetPlace.remove(purpleCarCheckBox);
        });

        builder.setView(dialogView);
        builder.setTitle("Select Car and Bet");
        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        startButton.setOnClickListener(view -> {
            // If user press start while the race is staring
            if (blackCar.getY() != 1200 && finishingTimes.size() < 4) {
                Toast.makeText(WelcomeActivity.this, "Please wait to the race end", Toast.LENGTH_SHORT).show();
                return;
            }

            // If user don't reset the previous race
            if (blackCar.getY() != 1200) {
                Toast.makeText(WelcomeActivity.this, "Please reset before start new race", Toast.LENGTH_SHORT).show();
                return;
            }

            // If user don't check any car and click start
            if (!blackCarCheckBox.isChecked() && !blueCarCheckBox.isChecked() && !greenCarCheckBox.isChecked() && !purpleCarCheckBox.isChecked()) {
                Toast.makeText(WelcomeActivity.this, "Bet at least one car to start race!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate if user checked but don't input money
            if (blackCarCheckBox.isChecked() && blackCarBetAmount.getText().toString().isEmpty()) {
                blackCarBetAmount.setError("This field is required");
                return;
            } else if (blackCarCheckBox.isChecked()){
                blackCarBet = Integer.parseInt(blackCarBetAmount.getText().toString());
            }
            if (blueCarCheckBox.isChecked() && blueCarBetAmount.getText().toString().isEmpty()) {
                blueCarBetAmount.setError("This field is required");
                return;
            } else if (blueCarCheckBox.isChecked()){
                blueCarBet = Integer.parseInt(blueCarBetAmount.getText().toString());
            }
            if (greenCarCheckBox.isChecked() && greenCarBetAmount.getText().toString().isEmpty()) {
                greenCarBetAmount.setError("This field is required");
                return;
            } else if (greenCarCheckBox.isChecked()){
                greenCarBet = Integer.parseInt(greenCarBetAmount.getText().toString());
            }
            if (purpleCarCheckBox.isChecked() && purpleCarBetAmount.getText().toString().isEmpty()) {
                purpleCarBetAmount.setError("This field is required");
                return;
            } else if (purpleCarCheckBox.isChecked()){
                purpleCarBet = Integer.parseInt(purpleCarBetAmount.getText().toString());
            }
//            if (blackCarBet == 0 || blueCarBet == 0 || greenCarBet == 0 || purpleCarBet == 0) {
//                Toast.makeText(WelcomeActivity.this, "Bet money must be greater than 0", Toast.LENGTH_SHORT).show();
//                return;
//            }

            totalBetAmount = blackCarBet + blueCarBet + greenCarBet + purpleCarBet;

            // Validate the bet money must be <= user balance
            if (totalBetAmount > loggedInAccount.getBalance()) {
                Toast.makeText(WelcomeActivity.this, "Your balance is not enough", Toast.LENGTH_SHORT).show();
            } else {
                moveCarWithRandomSpeed(blackCar);
                moveCarWithRandomSpeed(blueCar);
                moveCarWithRandomSpeed(greenCar);
                moveCarWithRandomSpeed(purpleCar);
                //Minus totalBetAmount when race started
                int leftBalance = loggedInAccount.getBalance() - totalBetAmount;
                loggedInAccount.setBalance(leftBalance);
                showBalance.setText("Balance: " + leftBalance);
                Toast.makeText(WelcomeActivity.this, "Race started", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    //Show balance popup
    private void showBalancePopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_balance_popup);
        TextView balanceTextView = dialog.findViewById(R.id.balanceTextView);
        final SeekBar addBalanceSeekBar = dialog.findViewById(R.id.addBalanceSeekBar);
        final TextView selectedBalanceTextView = dialog.findViewById(R.id.selectedBalanceTextView);
        Button addBalanceButton = dialog.findViewById(R.id.addBalanceButton);
        Button closeButton = dialog.findViewById(R.id.close_button);

        balanceTextView.setText("Current Balance: $" + loggedInAccount.getBalance());
        addBalanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedBalance = progress; // Update the selected balance
                selectedBalanceTextView.setText("Selected Amount: $" + selectedBalance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Add balance
        addBalanceButton.setOnClickListener(view -> {
            loggedInAccount.setBalance(loggedInAccount.getBalance() + selectedBalance);
            balanceTextView.setText("Current Balance: $" + loggedInAccount.getBalance());
            showBalance.setText("Balance: " + loggedInAccount.getBalance());
        });

        //Close popup + update balance
        closeButton.setOnClickListener(view -> {
            showBalance.setText("Balance: " + loggedInAccount.getBalance());
            dialog.dismiss();
        });
        dialog.show();
    }


    //Move car in 1600*900 pixel screen, from 20-30s
    private void moveCarWithRandomSpeed(ImageView car) {
        carAnimator = ObjectAnimator.ofFloat(car, "translationY",-860f);
        carAnimator.setDuration(new Random().nextInt(30000 - 20000 + 1) + 20000);
        carAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                long currentTime = System.currentTimeMillis();
                finishingTimes.put(car, currentTime);
                if (finishingTimes.size() == 4) {
                    List<ImageView> sortedCars = new ArrayList<>(finishingTimes.keySet());
                    Collections.sort(sortedCars, (car1, car2) ->
                            Long.compare(finishingTimes.get(car1), finishingTimes.get(car2)));
                    showFinishingOrder(sortedCars);
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        carAnimator.start();
    }

    private void showFinishingOrder(List<ImageView> sortedCars) {
        View dialogView = getLayoutInflater().inflate(R.layout.finishing_order_dialog, null);
        TextView orderTextView = dialogView.findViewById(R.id.finishingOrder);
        StringBuilder orderText = new StringBuilder("Finishing Order:\n");

        List<ImageView> finishCarImg = new ArrayList<>();
        finishCarImg.add(dialogView.findViewById(R.id.firstCarImg));
        finishCarImg.add(dialogView.findViewById(R.id.secondCarImg));
        finishCarImg.add(dialogView.findViewById(R.id.thirdCarImg));
        finishCarImg.add(dialogView.findViewById(R.id.fouthCarImg));

        ArrayList<TextView> finishCarText = new ArrayList<>();
        finishCarText.add(dialogView.findViewById(R.id.firstCarText));
        finishCarText.add(dialogView.findViewById(R.id.secondCarText));
        finishCarText.add(dialogView.findViewById(R.id.thirdCarText));
        finishCarText.add(dialogView.findViewById(R.id.fouthCarText));

        for (int i = 0; i < sortedCars.size(); i++) {
            finishCarImg.get(i).setImageDrawable(sortedCars.get(i).getDrawable());
            finishCarText.get(i).setText(sortedCars.get(i).getTag().toString());
        }

        orderTextView.setText(orderText.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(WelcomeActivity.this, "Win money: " + calculateWinningMoney(sortedCars), Toast.LENGTH_SHORT).show();
        TextView summaryResult = dialogView.findViewById(R.id.textViewResult);
        if (calculateWinningMoney(sortedCars) > totalBetAmount) {
            int moneyResult = calculateWinningMoney(sortedCars) - totalBetAmount;
            summaryResult.setText("Congratulation, you won " + moneyResult + "$");
            summaryResult.setTextColor(Color.parseColor("#51F349"));
        } else {
            int moneyResult = totalBetAmount - calculateWinningMoney(sortedCars);
            summaryResult.setText("Unfortunately, you lose " + moneyResult + "$");
            summaryResult.setTextColor(Color.parseColor("#F51425"));
        }
        // Sum user balance to winning money after the race finished
        loggedInAccount.setBalance(loggedInAccount.getBalance() + calculateWinningMoney(sortedCars));
        showBalance.setText("Balance: " + loggedInAccount.getBalance());

        // After the race finished, confirm the result
        Button confirmReslutButton = dialogView.findViewById(R.id.confirmResultBtn);
        confirmReslutButton.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }
    private int calculateWinningMoney(List<ImageView> sortedCar) {
        int winningMoney = 0;

        for (CheckBox c : carBetPlace) {
            for (ImageView img : sortedCar) {
                if (returnImgViewBaseOnCheckbox(c).getTag().equals(img.getTag())) {
                    if (sortedCar.indexOf(img) == 0) { // 1st car
                       winningMoney += returnMoneyBaseOnCheckBox(c) * 2 ;
                       break;
                    } else if (sortedCar.indexOf(img) == 1) {
                        winningMoney += (int) Math.round(returnMoneyBaseOnCheckBox(c) * 1.5); // 2nd car
                        break;
                    } else {
                        winningMoney += 0; // 3rd and 4th cars
                        break;
                    }
                }
            }
        }
        return winningMoney;
    }

    private ImageView returnImgViewBaseOnCheckbox(CheckBox checkBox) {
        ImageView imageView;
        if (getResources().getResourceEntryName(checkBox.getId()).equals("checkbox_blackcar")) {
            imageView = findViewById(R.id.imageView4);
        } else if (getResources().getResourceEntryName(checkBox.getId()).equals("checkbox_bluecar")) {
            imageView = findViewById(R.id.imageView5);
        } else if (getResources().getResourceEntryName(checkBox.getId()).equals("checkbox_greencar")) {
            imageView = findViewById(R.id.imageView6);
        } else {
            imageView = findViewById(R.id.imageView7);
        }
        return imageView;
    }

    private int returnMoneyBaseOnCheckBox(CheckBox checkBox) {
        int betMoney = 0;
        if (getResources().getResourceEntryName(checkBox.getId()).equals("checkbox_blackcar")) {
            betMoney = blackCarBet;
        } else if (getResources().getResourceEntryName(checkBox.getId()).equals("checkbox_bluecar")) {
            betMoney = blueCarBet;
        } else if (getResources().getResourceEntryName(checkBox.getId()).equals("checkbox_greencar")) {
            betMoney = greenCarBet;
        } else {
            betMoney = purpleCarBet;
        }
        return betMoney;
    }
}