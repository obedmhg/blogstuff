// ==UserScript==
// @name          Append ProjectId BCC
// @homepageURL  https://github.com/obedmhg/blogstuff
// @supportURL   https://github.com/obedmhg/blogstuff/issues
// @description   It will appen the project Id to the project name.
// @version       0.0.1
// @include       /atg/
// @author        Obed Murillo
// @require http://code.jquery.com/jquery-latest.js
// @grant none
// @updateUrl   https://raw.githubusercontent.com/obedmhg/blogstuff/master/atgstuff/showProjectId.js
// @downloadUrl https://raw.githubusercontent.com/obedmhg/blogstuff/master/atgstuff/showProjectId.js
// ==/UserScript==

(function ($, undefined) {
  $(function () {
      $('a').each(function() {
          if (this.href.indexOf("project=") >= 0 && this.text.indexOf("Author") < 0 && this.text.indexOf("Verify ") < 0 && this.text.indexOf("SEOURLGeneration") < 0 && this.text.indexOf("Accept ") < 0) {
              var afterProject = this.href.split("project=");
              var projectId = afterProject[1].split("&")[0];
              $(this).after( "<div style='float: right; border: 1px solid white; background-color: #166297; color: white; width: 65px; height: 12px;  text-align: center; vertical-align: middle;line-height: 12px; '>"+projectId+" </div>" );
          }
      });
  });
})(window.jQuery.noConflict(true));
