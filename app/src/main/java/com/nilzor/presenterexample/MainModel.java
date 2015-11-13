package com.nilzor.presenterexample;

import android.content.res.Resources;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MainModel {
    public ObservableField<String> numberOfUsersLoggedIn = new ObservableField<>();
    public ObservableField<Boolean> isExistingUserChecked = new ObservableField<>();
    public ObservableField<Integer> emailBlockVisibility = new ObservableField<>();
    public ObservableField<String> loginOrCreateButtonText = new ObservableField<>();
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    private boolean mIsLoaded;
    private ToastPresenter mToastPresenter;
    private Resources mResources;

    public MainModel(ToastPresenter toastPresenter, Resources resources) {
        mToastPresenter = toastPresenter;
        mResources = resources; // You might want to abstract this for testability
        setInitialState();
        updateDependentViews();
        hookUpDependencies();
    }
    public boolean isLoaded() {
        return mIsLoaded;
    }

    private void setInitialState() {
        numberOfUsersLoggedIn.set("...");
        isExistingUserChecked.set(true);
    }

    private void hookUpDependencies() {
        isExistingUserChecked.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                updateDependentViews();
            }
        });
        username.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Log.d("Model", "Username now: " + username.get());
            }
        });
    }

    public void updateDependentViews() {
        updateDependentViews(isExistingUserChecked.get());
    }

    public void updateDependentViews(boolean isExistingUserChecked) {
        if (isExistingUserChecked) {
            emailBlockVisibility.set(View.GONE);
            loginOrCreateButtonText.set(mResources.getString(R.string.log_in));
        }
        else {
            emailBlockVisibility.set(View.VISIBLE);
            loginOrCreateButtonText.set(mResources.getString(R.string.create_user));
        }
    }

    public void updateDependentViews(View view, boolean isExistingUserCheecked) {
        updateDependentViews(isExistingUserCheecked);
    }

    public void loadAsync() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Simulating some asynchronous task fetching data from a remote server
                try {Thread.sleep(2000);} catch (Exception ex) {};
                numberOfUsersLoggedIn.set("" + new Random().nextInt(1000));
                mIsLoaded = true;
                return null;
            }
        }.execute((Void) null);
    }

    public void logInClicked(View view) {
        // Illustrating the need for calling back to the view though testable interfaces.
        if (isExistingUserChecked.get()) {
            mToastPresenter.showShortToast("Invalid username or password");
        }
        else {
            mToastPresenter.showShortToast("Please enter a valid email address");
        }
    }
}
