## Architecture Components

- Lifecycle-aware components
- ViewModels
- LiveData
- Flow
- DataBinding
- Navigation

## Libraries

- DaggerHilt
- Retrofit
- Shimmer
- Material design
- Viewpager2
- Leak canary
- Coroutine
- Okhttp
- Glide
- Chuck


## MVI Architecture

In Model-View-Intent architecture, view exposes view-events (user input/action) and observes model for view-state changes. We process view-events and convert them into respective intent and pass it to the model. The model layer creates a new immutable view-state using intent and previous view-state. So, this way it follows the Unidirectional Data Flow principle i.e. data flows only in one direction: View > Intent > Model > View.


<img src="https://miro.medium.com/max/4800/1*w0QeeQqrnISXLhYkYZWoAg.png" width="400">
 
 Create ViewModels by Extending this class.

 **STATE** ViewState should represent the current state of the view at any given time.
 So this class should have all the variable content on which our view is dependent.
 Every time there is any user input/action we will expose modified
 copy (to maintain the previous state which is not being modified) of this class.
 We can create this model using Kotlin's data class.

 **EFFECT**  ViewEffect is useful for actions that are fire-and-forget and we do not
 want to maintain its state. We can create this class using Kotlin's sealed class.

 **EVENT**  Represents all actions/events a user can perform on the view.
 This is used to pass user input/action to the ViewModel.
 We can create this event set using Kotlin's sealed class.

 **process(viewEvent: EVENT)** Process ViewEvents (viewEvent) passed by Activity/Fragment/View
                                     and update ViewState and ViewEffect accordingly.

 Some articles:
 
  - <a href="https://medium.com/@rohitss/best-architecture-for-android-mvi-livedata-viewmodel-71a3a5ac7ee3">Custom MVI architecture</a>
  - <a href="http://hannesdorfmann.com/android/model-view-intent/">Modal-View-Intent</a>
