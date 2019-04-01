package net.rgsw.minesweeper.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.ECellState;
import net.rgsw.minesweeper.settings.Configuration;

public class TutorialActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static final ECellState URV = ECellState.UNREVEALED;
    private static final ECellState MIN = ECellState.MINE;
    private static final ECellState RV0 = ECellState.NOTHING;
    private static final ECellState RV1 = ECellState.FOUND_1;
    private static final ECellState RV2 = ECellState.FOUND_2;
    private static final ECellState RV3 = ECellState.FOUND_3;
    private static final ECellState RV4 = ECellState.FOUND_4;
    private static final ECellState RV5 = ECellState.FOUND_5;
    private static final ECellState RV6 = ECellState.FOUND_6;
    private static final ECellState RV7 = ECellState.FOUND_7;
    private static final ECellState RV8 = ECellState.FOUND_8;
    private static final ECellState FLG = ECellState.FLAGGED;
    private static final ECellState FMN = ECellState.FLAGGED_MINE;
    private static final ECellState MWN = ECellState.MINE_WIN;

    private static final ECellState[][] states = {
            {
                    URV, URV, URV, URV, URV,
                    URV, URV, URV, URV, URV,
                    URV, URV, URV, URV, URV,
                    URV, URV, URV, URV, URV,
                    URV, URV, URV, URV, URV,
            },
            {
                    MIN, MIN, URV, URV, URV,
                    MIN, URV, URV, MIN, URV,
                    URV, URV, URV, URV, URV,
                    URV, URV, URV, URV, URV,
                    URV, URV, URV, URV, MIN,
            },
            {
                    MIN, MIN, RV2, RV1, RV1,
                    MIN, RV3, RV2, MIN, RV1,
                    RV1, RV1, RV1, RV1, RV1,
                    RV0, RV0, RV0, RV1, RV1,
                    RV0, RV0, RV0, RV1, MIN,
            },
            {
                    FLG, FLG, RV2, RV1, RV1,
                    FLG, RV3, RV2, FLG, RV1,
                    RV1, RV1, RV1, RV1, RV1,
                    RV0, RV0, RV0, RV1, RV1,
                    RV0, RV0, RV0, RV1, FLG,
            },
            {},
            {
                    URV, URV, URV, URV, URV,
                    URV, URV, RV1, FLG, RV1,
                    URV, URV, RV1, RV1, RV1,
                    URV, URV, RV1, RV2, RV1,
                    URV, URV, URV, URV, URV,
            },
            {
                    FMN, FMN, RV2, RV1, RV1,
                    FMN, RV3, RV2, FMN, RV1,
                    RV1, RV1, RV1, RV1, RV1,
                    RV0, RV0, RV0, RV1, RV1,
                    RV0, RV0, RV0, RV1, MWN,
            },
            {
                    RV2, MIN, RV4, MIN, RV2,
                    RV2, MIN, RV4, MIN, RV2,
                    RV2, RV2, RV2, RV2, RV2,
                    MIN, RV3, RV3, RV3, MIN,
                    RV2, MIN, MIN, MIN, RV2,
            }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setTheme( Configuration.useDarkTheme.getValue() ? R.style.AppTheme_Dark : R.style.AppTheme );
        setContentView( R.layout.activity_tutorial );

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById( R.id.container );
        mViewPager.setAdapter( mSectionsPagerAdapter );

    }

    public void skip( View v ) {
        finish();
    }

    public void tryone( View v ) {
        setResult( RESULT_OK );
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance( int sectionNumber ) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt( ARG_SECTION_NUMBER, sectionNumber );
            fragment.setArguments( args );
            return fragment;
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
            int page = getArguments().getInt( ARG_SECTION_NUMBER );
            View rootView;
            if( page == 1 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_1, container, false );
            } else if( page == 2 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_2, container, false );
            } else if( page == 3 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_3, container, false );
            } else if( page == 4 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_4, container, false );
            } else if( page == 5 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_5, container, false );
            } else if( page == 6 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_6, container, false );
            } else if( page == 7 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_7, container, false );
            } else if( page == 8 ) {
                rootView = inflater.inflate( R.layout.tutorial_page_8, container, false );
            } else {
                rootView = inflater.inflate( R.layout.fragment_tutorial, container, false );
            }

            TutorialCanvasLegacy canvas = rootView.findViewById( R.id.tutorialCanvas );

            if( canvas != null && page - 1 < states.length ) {
                ( ( TutorialGame ) canvas.getGame() ).setStates( states[ page - 1 ] );
                canvas.invalidate();
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter( FragmentManager fm ) {
            super( fm );
        }

        @Override
        public Fragment getItem( int position ) {
            return PlaceholderFragment.newInstance( position + 1 );
        }

        @Override
        public int getCount() {
            return 8;
        }
    }
}
