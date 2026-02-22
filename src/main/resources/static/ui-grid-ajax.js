(function () {
  "use strict";

  function initGridAjax() {
    document.querySelectorAll("form.sort-inline[data-grid]").forEach(function (form) {
      if (form.dataset.ajaxBound === "1") {
        return;
      }
      form.dataset.ajaxBound = "1";
      form.addEventListener("submit", function (event) {
        event.preventDefault();
        loadGrid(form.getAttribute("action"), new FormData(form), form.dataset.grid);
      });
    });

    document.querySelectorAll(".pager a").forEach(function (link) {
      if (link.dataset.ajaxBound === "1") {
        return;
      }
      link.dataset.ajaxBound = "1";
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

    bindAdDismiss();
    bindToastAutoHide();
  }

  function loadGrid(url, formData, grid) {
    var targetCard = grid === "orders"
        ? document.getElementById("orders-grid-card") || findGridCard("orders")
        : document.getElementById("expenses-grid-card") || findGridCard("expenses");
    if (!targetCard) {
      window.location.href = url;
      return;
    }
    setLoading(targetCard, true);

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
        })
        .finally(function () {
          setLoading(targetCard, false);
        });
  }

  function setLoading(card, isLoading) {
    if (!card) {
      return;
    }
    card.classList.toggle("is-loading", isLoading);
    card.querySelectorAll("button, select, input, a").forEach(function (el) {
      if (el.tagName === "A") {
        el.style.pointerEvents = isLoading ? "none" : "";
        return;
      }
      el.disabled = !!isLoading;
    });
  }

  function bindAdDismiss() {
    var ad = document.querySelector(".ad-rail");
    if (!ad) {
      return;
    }
    if (sessionStorage.getItem("adDismissed") === "1") {
      ad.style.display = "none";
      return;
    }
    var btn = ad.querySelector(".ad-dismiss");
    if (!btn || btn.dataset.bound === "1") {
      return;
    }
    btn.dataset.bound = "1";
    btn.addEventListener("click", function () {
      sessionStorage.setItem("adDismissed", "1");
      ad.style.display = "none";
    });
  }

  function bindToastAutoHide() {
    document.querySelectorAll(".toast").forEach(function (toast) {
      if (toast.dataset.bound === "1") {
        return;
      }
      toast.dataset.bound = "1";
      setTimeout(function () {
        toast.style.display = "none";
      }, 2800);
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
