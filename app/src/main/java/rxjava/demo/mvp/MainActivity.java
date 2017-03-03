/*
 * Copyright (c) 2017. Al Warren.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rxjava.demo.mvp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import rxjava.demo.mvp.framework.presenter.MainActivityPresenter;
import rxjava.demo.mvp.framework.view.MainActivityView;
import rxjava.demo.mvp.framework.model.item.Item;
import rxjava.demo.mvp.framework.model.repository.FileRepository;
import rxjava.demo.mvp.framework.model.repository.Repository;
import rxjava.demo.mvp.util.FileUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * A simple activity to demonstrate MVP with RxJava2 for async operations.
 *
 * It uses a repository pattern that simulates reading a directory
 * off the main thread.
 *
 * Thanks to the Dry Culture YouTube channel and Rakesh Patel.
 * @see <a href="http://y2u.be/JwBGnN06Kso">http://y2u.be/JwBGnN06Kso</a>
 */
public class MainActivity extends AppCompatActivity
        implements MainActivityView {

    MainActivityPresenter presenter;
    Repository repository = new FileRepository();

    private TextView mTextViewLog;
    private ScrollView mScrollView;
    private ProgressBar spinner;
    private TextView mHelloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        initializePresenter();
    }

    public void initializeViews() {
        mHelloTextView =
                (TextView) findViewById(R.id.hello_text);
        mTextViewLog =
                (TextView) findViewById(R.id.text_output);
        mScrollView =
                (ScrollView) findViewById(R.id.scrollview_text_output);
        spinner =
                (ProgressBar) findViewById(R.id.progressBar1);

        spinner.setVisibility(View.GONE);
    }

    private void resetScrollView() {
        mTextViewLog.setText("");
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void initializePresenter() {
        presenter = new MainActivityPresenter(this, repository,
                // pass thread scheduler to presenter so it doesn't break testing
                AndroidSchedulers.mainThread());

        presenter.loadItems(FileUtils.basePath(this));
    }

    private void reloadItems() {
        resetScrollView();
        initializePresenter();
    }

    @Override
    public void displayItems(List<Item> itemList) {
        for (Item item : itemList) {
            println(item.toString());
        }
    }

    @Override
    public void displayNoItems() {
        println("No Items to get");
    }

    @Override
    public void displayError() {
        println("An error occurred loading items");
    }

    @Override
    public void showProgress() {
        mHelloTextView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        spinner.setVisibility(View.GONE);
        mHelloTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_log:
                mTextViewLog.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                return true;
            case R.id.reload_log:
                reloadItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Utility method to print output
     *
     * @param input
     */
    public void println(final String input) {
        new Handler()
                .post(new Runnable() {
                            @Override
                            public void run() {
                                mTextViewLog.append(input + "\n");
                                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        }
                );
    }

}
