(function () {
  "use strict";

  function initGridAjax() {
    document.querySelectorAll("form.sort-inline[data-grid]").forEach(function (form) {
      form.addEventListener("submit", function (event) {
        event.preventDefault();
        loadGrid(form.getAttribute("action"), new FormData(form), form.dataset.grid);
      });
    });

    document.querySelectorAll(".pager a").forEach(function (link) {
      link.addEventListener("click", function (event) {
        var section = link.closest("section.card");
        if (!section) {
          return;
        }
        var sortForm = section.querySelector("form.sort-inline[data-grid]");
        if (!sortForm) {
          return;
        }
        event.preventDefault();
        loadGrid(link.getAttribute("href"), null, sortForm.dataset.grid);
      });
    });
  }

  function loadGrid(url, formData, grid) {
    var targetCard = grid === "orders"
        ? document.getElementById("orders-grid-card") || findGridCard("orders")
        : document.getElementById("expenses-grid-card") || findGridCard("expenses");
    if (!targetCard) {
      window.location.href = url;
      return;
    }

    var fetchUrl = url;
    if (formData) {
      var params = new URLSearchParams(formData);
      fetchUrl += (fetchUrl.indexOf("?") === -1 ? "?" : "&") + params.toString();
    }

    fetch(fetchUrl, {
      method: "GET",
      headers: { "X-Requested-With": "XMLHttpRequest" }
    })
        .then(function (response) {
          if (!response.ok) {
            throw new Error("Grid request failed");
          }
          return response.text();
        })
        .then(function (html) {
          var doc = new DOMParser().parseFromString(html, "text/html");
          var replacement = grid === "orders"
              ? doc.getElementById("orders-grid-card") || findGridCard("orders", doc)
              : doc.getElementById("expenses-grid-card") || findGridCard("expenses", doc);

          if (!replacement) {
            window.location.href = fetchUrl;
            return;
          }

          targetCard.outerHTML = replacement.outerHTML;
          history.replaceState({}, "", fetchUrl);
          initGridAjax();
        })
        .catch(function () {
          window.location.href = fetchUrl;
        });
  }

  function findGridCard(grid, rootDoc) {
    var root = rootDoc || document;
    var form = root.querySelector('form.sort-inline[data-grid="' + grid + '"]');
    return form ? form.closest("section.card") : null;
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initGridAjax);
  } else {
    initGridAjax();
  }
})();
