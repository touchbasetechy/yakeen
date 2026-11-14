// This is a manifest file that'll be compiled into application.js.
//
//= require jquery-3.5.1.min
//= require bootstrap.bundle.min
//= require_self

'use strict';

function isScrolledToBottom(el) {
  return el.scrollHeight - el.scrollTop - el.clientHeight < 2;
}

function handleTabSelection(tabChildSelector, localStorageKey, cardContainerDiv, tableContainerDiv) {
  $(tabChildSelector + ' a').on('shown.bs.tab', function (e) {
    const target = $(e.target).attr('href');
    localStorage.setItem(localStorageKey, target.substring(1));
  });

  const selectedTab = localStorage.getItem(localStorageKey);

  if (selectedTab) {
    $(tabChildSelector + ' a[href="#' + selectedTab + '"]').tab('show');
    if (selectedTab === "tab2") {
      cardContainerDiv.focus();
    } else if (selectedTab === "tab1") {
      tableContainerDiv.focus();
    }
  } else {
    $(tabChildSelector + ' a[href="#tab1"]').tab('show');
    tableContainerDiv.focus();
  }
}
