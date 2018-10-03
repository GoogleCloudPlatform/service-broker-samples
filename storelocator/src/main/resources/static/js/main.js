/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

let map;
let placesService;
let search;

const resultsList = new Vue({
  el: '#results-list',
  data: {
    items: [],
    markers: [],
    selected: -1,
  },
  methods: {
    highlight: function (i) {
      selectLocation(i);
    },
  },
});

const markerIcon = {
  url: '/pin.png',
  size: {width: 23, height: 32},
  origin: {x: 0, y: 0},
  anchor: {x: 12, y: 32},
};

// Callback function for the Google Maps script.
function initMap() {

  // Attempt to get the user's location.
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function (position) {
      state.map.lat = position.coords.latitude;
      state.map.lng = position.coords.longitude;
    });
  }

  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: state.map.lat, lng: state.map.lng},
    zoom: state.map.zoomLevel,
  });

  placesService = new google.maps.places.PlacesService(map);

  const input = document.getElementById('search');
  search = new google.maps.places.SearchBox(input);

  google.maps.event.addListener(map, 'bounds_changed', function () {
    getLocations(map.getBounds())
  });

}

// Calls the backend for location objects.
function getLocations(bounds) {

  const url = "/api/v1/stores?lat1=" + bounds.getNorthEast().lat() + "&lat2="
      + bounds.getSouthWest().lat() + "&lng1=" + bounds.getNorthEast().lng()
      + "&lng2=" + bounds.getSouthWest().lng();

  // Query the backend to refresh results.
  $.getJSON(url, updateView);
}

// Updates the Vue data
function updateView(results) {

  // Find which location is selected
  let selectedName;
  if (resultsList.selected !== -1 && resultsList.items.length
      > resultsList.selected) {
    selectedName = resultsList.items[resultsList.selected].name
  } else {
    selectedName = null;
    resultsList.selected = -1;
  }

  // If the location still exists, keep it selected
  for (let i = 0; i < results.length; i++) {
    if (selectedName === results[i].name) {
      resultsList.selected = i;
      break;
    }
  }

  const names = [];
  const newNames = [];
  for (let i = 0; i < resultsList.items.length; i++) {
    names[i] = resultsList.items[i].name;
  }

  // Update markers
  for (let i = 0; i < results.length; i++) {

    newNames.push(results[i].name);

    if (names.includes(results[i].name)) {
      continue;
    }

    // Add a marker
    const marker = new google.maps.Marker({
      position: {
        lat: results[i].latitude,
        lng: results[i].longitude
      },
      map: map,
      icon: markerIcon,
    });

    marker.addListener('click', function () {
      selectLocation(i);
    });

    resultsList.markers.push(marker);
  }

  for (let i = 0; i < resultsList.items.length; i++) {
    if (!newNames.includes(resultsList.items[i].name)) {
      resultsList.markers[i].setMap(null);
    }
  }

  for (let i = 0; i < resultsList.markers.length; i++) {
    let icon;
    if (i === resultsList.selected) {
      icon = null;
    } else {
      icon = markerIcon;
    }
    resultsList.markers[i].setIcon(icon);
  }

  resultsList.items = results;
}

function selectLocation(index) {
  resultsList.selected = index;
  updateView(resultsList.items);
}
