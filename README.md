# bomeans_sdk_ac_demo_android
This demo program demonstrates how to make your GUI to interact with the Bomeans IR SDK.

Remote controllers of air conditioners act very different from general TV remote controllers. For TV remote controller (and the like), pressing a key will transmit a fixed IR waveform. But for typical remote controllers for the air conditioners, especially those controllers with LCD display panel, do not transmit fixed IR waveform when you press a key. Generally speaking, everytime you press a key, the remote controller will collect its internal states such as the power state, mode state, temperature, fan speed, air swing, etc. to compose the IR signals. This signals is then transmitted to synchronize the states of the target air conditioner.

The Bomeans IR database and the associated SDK is then design to handle this situation in order to allow the user to create remote controllers that behave just like the real remote controller.

Three demo activities are included:
* Demo 1: Basic demo. The temperature is mapped into temp+ and temp- keys just like the ordinary physical AC remote controllers.
* Demo 2: Similar to Demo 1, but using a wrapper for the generated remote to make the temperature keys mapping easier.
* Demo 3: Similar to Demo 1, but using slide bar (seek bar) for the temperature setting.

[Note] You need to apply an Bomeans IR API Key for this demo code to run.

<img src="screenshot.jpg" width="400">
