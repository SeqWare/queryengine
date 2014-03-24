'use strict';

/* https://github.com/angular/protractor/blob/master/docs/getting-started.md */

describe('my app', function() {

  browser.get('index.html');

  it('should automatically redirect to / when location hash/fragment is empty', function() {
    expect(browser.getLocationAbsUrl()).toMatch("/");
  });


  describe('query', function() {

    beforeEach(function() {
      browser.get('index.html#/query');
    });


    it('should render query when user navigates to /query', function() {
      expect(element.all(by.css('[ng-view] p')).first().getText()).
        toMatch(/partial for query/);
    });

  });


  describe('upload', function() {

    beforeEach(function() {
      browser.get('index.html#/upload');
    });


    it('should render upload when user navigates to /upload', function() {
      expect(element.all(by.css('[ng-view] p')).first().getText()).
        toMatch(/partial for upload/);
    });

  });
});
