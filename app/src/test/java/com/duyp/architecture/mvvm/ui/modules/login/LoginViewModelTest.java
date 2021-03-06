package com.duyp.architecture.mvvm.ui.modules.login;


import com.duyp.androidutils.StringUtils;
import com.duyp.architecture.mvvm.BaseViewModelTest;
import com.duyp.architecture.mvvm.dagger.TestAppComponent;
import com.duyp.architecture.mvvm.data.model.User;
import com.duyp.architecture.mvvm.data.source.State;

import org.junit.Test;

import static com.duyp.architecture.mvvm.test_utils.ModelTestUtils.sampleUser;
import static com.duyp.architecture.mvvm.test_utils.RemoteTestUtils.errorResponse;
import static com.duyp.architecture.mvvm.test_utils.RemoteTestUtils.successResponse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;
/**
 * Created by duypham on 10/21/17.
 *
 */

public class LoginViewModelTest extends BaseViewModelTest<LoginViewModel> {

    @Override
    protected void inject(TestAppComponent testAppComponent) {
        testAppComponent.inject(this);
    }

    @Test
    public void loginSuccess() throws Exception {
        String pass = "abc";
        User user = sampleUser(1L);
        String token = StringUtils.getBasicAuth(user.getLogin(), pass);

        when(githubService.login(any())).thenReturn(successResponse(user));

        when(userManager.startUserSession(any(), any())).thenReturn(null);

        viewModel.setUserName(user.login);
        viewModel.setPassword(pass);
        viewModel.login();

        verify(stateLiveData, times(1)).setValue(State.loading(null));
        verify(stateLiveData, times(1)).setValue(State.success(null));

        verify(userManager).startUserSession(user, token);
        verify(mNavigatorHelper).navigateMainActivity(true);
    }

    @Test
    public void loginError() throws Exception {
        when(githubService.login(any())).thenReturn(errorResponse(401, "blah blah"));

        viewModel.login();

        verify(stateLiveData, times(1)).setValue(State.loading(null));
        verify(stateLiveData, times(1)).setValue(State.error("blah blah"));

        verifyZeroInteractions(userManager);
    }
}