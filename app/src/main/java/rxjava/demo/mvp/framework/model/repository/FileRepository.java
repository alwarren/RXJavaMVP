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

package rxjava.demo.mvp.framework.model.repository;


import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import rxjava.demo.mvp.framework.model.item.Item;
import rxjava.demo.mvp.framework.model.item.MockItems;
import io.reactivex.Single;

/**
 * A repository implementation that simulates reading a directory listing.
 */
public class FileRepository implements Repository {
    @Override
    public Single<List<Item>> getItems(final File folder) {
        // Use an RxJava2 Single Value Response object
        return Single.fromCallable(new Callable<List<Item>>() {
            @Override
            public List<Item> call() throws Exception {
                // Simulate latency
                Thread.sleep(3000);
                // Fetch some mock items
                return MockItems.mock(folder);
            }
        });
    }
}
