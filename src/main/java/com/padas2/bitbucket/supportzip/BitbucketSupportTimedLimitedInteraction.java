package com.padas2.bitbucket.supportzip;

import com.padas2.bitbucket.supportzip.response.BitbucketRestApiResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import org.apache.http.auth.AuthenticationException;
import java.io.IOException;
import java.util.concurrent.*;

abstract public class BitbucketSupportTimedLimitedInteraction<T> extends BitbucketSupportInteraction implements TimeRestrained{
    public BitbucketSupportTimedLimitedInteraction(BitbucketServerDetails bitbucketServerDetails) {
        super(bitbucketServerDetails);
    }

    protected abstract void mainMethod();

    @Override
    public BitbucketRestApiResponse run() throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future future = executorService.submit(new Runnable() {
            @Override
            public void run() {
                mainMethod();
            }
        });

        try {
            future.get(1, TimeUnit.MINUTES);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            future.cancel(true);
            e.printStackTrace();
            throw e;
        } finally {
            executorService.shutdown();
            if(authenticationException != null)
                throw authenticationException;
            if(ioException != null)
                throw ioException;
        }
        return bitbucketRestApiResponse;
    }
}
