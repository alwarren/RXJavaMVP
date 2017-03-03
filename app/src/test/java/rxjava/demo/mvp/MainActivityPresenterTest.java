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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.util.Collections;
import java.util.List;

import rxjava.demo.mvp.framework.presenter.MainActivityPresenter;
import rxjava.demo.mvp.framework.view.MainActivityView;
import rxjava.demo.mvp.framework.model.item.Item;
import rxjava.demo.mvp.framework.model.repository.Repository;
import rxjava.demo.mvp.framework.model.item.MockItems;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainActivityPresenterTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock Repository repository;
    @Mock MainActivityView view;
    @Mock File folder;

    private MainActivityPresenter presenter;

    private final List<Item> MANY_ITEMS = MockItems.mock();
    private final List<Item> NO_ITEMS = Collections.emptyList();

    @Before
    public void setUp() {
        // Pass the trampoline scheduler to the observer for testing
        presenter = new MainActivityPresenter(view, repository, Schedulers.trampoline());
        // Change subscriber scheduler to match observer for testing
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
                return Schedulers.trampoline();
            }
        });
    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }

    @Test
    public void shouldPassItemsToView() throws Exception {
        when(repository.getItems(folder))
                .thenReturn(Single.just(MANY_ITEMS));

        presenter.loadItems(folder);

        verify(view).displayItems(MANY_ITEMS);
    }

    @Test
    public void shouldPassNoItemsToView() throws Exception {
        when(repository.getItems(folder))
                .thenReturn(Single.just(NO_ITEMS));

        presenter.loadItems(folder);

        verify(view).displayNoItems();
    }

    @Test public void shouldHandleError() throws Exception {
        when(repository.getItems(folder))
                .thenReturn(Single.<List<Item>>error(new Throwable("fail")));

        presenter.loadItems(folder);

        verify(view).displayError();
    }
}