
# AntiProxy
AntiProxy is a Velocity plugin aiming to block VPN user access to your server. To use this plugin you will need an api key that can be obtained at [ProxyCheck](https://proxycheck.io/) (its totally free!)


## Installation
- Generate a api key at [ProxyCheck](https://proxycheck.io/)
- Drag and drop the plugins into the plugin folder
- Reboot your server
- Modify the key inside the config.toml generated by the plugin
- Reboot once again.
- Enjoy!
## Information
The plugin will automatically log all ip's connected and define them as proxies or not, it will do that so that it can avoid making double API calls for the same ip. This is all stored in the H2 database.


