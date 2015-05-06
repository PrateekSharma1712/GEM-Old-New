package com.prateek.gem.views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.prateek.gem.BaseActivity;
import com.prateek.gem.OnModeConfirmListener;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.widgets.*;

/**
 * Created by prateek on 21/4/15.
 */
public abstract class MainActivity extends BaseActivity implements OnModeConfirmListener {

    protected Toolbar mToolBar = null;
    protected MainActivity base = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        mToolBar = (Toolbar) findViewById(R.id.vToolbar);
        DebugLogger.message("MainActivity :: toolbar" + mToolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        base = this;
    }

    protected abstract int getLayoutResource();

    protected void setToolbar(int titleId, int ic_group) {
        DebugLogger.message("MainActivity :: setToolbar :: toolbar"+mToolBar);
        setToolbar(getString(titleId), ic_group);
        mToolBar.setTitle(getString(titleId));
        mToolBar.setLogo(null);
    }

    protected void setToolbar(String titleName, int ic_group) {
        DebugLogger.message("MainActivity :: setToolbar :: toolbar"+mToolBar);
        mToolBar.setTitle(titleName);
        mToolBar.setLogo(null);
    }

    protected void setToolbar(String titleName) {
        DebugLogger.message("MainActivity :: setToolbar :: toolbar"+mToolBar);
        mToolBar.setTitle(titleName);
    }

    @Override
    public void modeConfirmed() {

    }

    @Override
    public void deleteMemberConfirmed(int memberId) {

    }

    @Override
    public void deleteExpenseConfirmed(int expenseId) {

    }

    @Override
    public void deleteItemConfirmed(boolean deleteItemConfirmed) {

    }

    @Override
    public void openNewActivity(int requestCodeClickImage) {

    }

    @Override
    public void onItemsAdded(String category, String item) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBImpl.closeDatabase();
    }
}
