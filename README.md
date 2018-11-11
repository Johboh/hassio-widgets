# hassio-widgets
Simple, naive and quite ugly Android widgets for Hass.io, where you can call a generic API endpoint.

You have the option to call any api with any payload. You can for example add a widget which will call `/api/services/scene/turn_on` and with the payoad `{"entity_id":"scene.movie"}` to turn on the movie sceen.

The REST API can be found at https://developers.home-assistant.io/docs/en/external_api_rest.html. Authentication is done using the api-key.
