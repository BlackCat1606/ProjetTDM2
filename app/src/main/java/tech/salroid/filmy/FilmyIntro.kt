package tech.salroid.filmy


import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentA
import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentB
import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentC
import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentD




class FilmyIntro : AppIntro2() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.


        addSlide(IntroFragmentA())
        addSlide(IntroFragmentB())
        addSlide(IntroFragmentC())
        addSlide(IntroFragmentD())

        showStatusBar(false)
        isProgressButtonEnabled = true


        //setZoomAnimation(); //OR
        //setFlowAnimation(); //OR
        //setSlideOverAnimation(); //OR
        setDepthAnimation()

    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        // Do something when users tap on Done button.
        finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        // Do something when the slide changes.
    }

}
