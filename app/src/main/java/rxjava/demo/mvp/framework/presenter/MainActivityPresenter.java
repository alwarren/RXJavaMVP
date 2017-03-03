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

package rxjava.demo.mvp.framework.presenter;

import java.io.File;
import java.util.List;

import rxjava.demo.mvp.framework.view.MainActivityView;
import rxjava.demo.mvp.framework.model.item.Item;
import rxjava.demo.mvp.framework.model.repository.Repository;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivityPresenter {
    private MainActivityView mView;
    private Repository mRepository;
    private Scheduler mViewScheduler;
    private CompositeDisposable mCompositeDisposable;

    /**
     * A simple presenter implementation that doesn't rely on any
     * Android dependencies.
     *
     * It uses RxJava2 for asynchronous operations.
     *
     * @param view interface
     * @param repository interface
     * @param viewScheduler RxJava2 abstract class
     */
    public MainActivityPresenter(MainActivityView view, Repository repository,
                                 Scheduler viewScheduler){
        mView = view;
        mRepository = repository;
        mViewScheduler = viewScheduler;
        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * Simulate reading a list of file paths from a directory.
     * Calls back to the main activity using the view interface
     *
     * @param folder
     */
    public void loadItems(File folder) {

        // Show the progress spinner
        mView.showProgress();

        mCompositeDisposable.add(

                // Get the directory listing from the file repository
                mRepository.getItems(folder)

                // Run async on the io scheduler
                .subscribeOn(Schedulers.io())

                // Use a constructor parameter for the observer scheduler
                // so we can change it in unit tests
                .observeOn(mViewScheduler)

                // Use a disposable observer with a list that expects
                // a single response object
                .subscribeWith(new DisposableSingleObserver<List<Item>>() {

                    // Call back to MainActivity via the View interface

                    @Override
                    public void onSuccess(List<Item> items) {
                        if (items.isEmpty()) {
                            mView.displayNoItems();
                        } else {
                            mView.displayItems(items);
                        }
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayError();
                        mView.hideProgress();
                    }
                }));
    }
}
