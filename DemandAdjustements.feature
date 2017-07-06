Feature: manual adjustments of demand

  sub domain: demand forecasting
  keeps track of current and future customer needs for our products

  Domain story:
  Adjust demand at day to amount, delivered.
  >> demand.adjust(productRefNo, atDay, amount)
  We can change only Demands for today and future.
  New demand is stored for further reference
  Data from callof document should be preserved (DON’T OVERRIDE THEM).
  Adjust demand should be possible to even
  if there was no callof document for that product.
  Logistician note should be kept along with adjustment.
  In standard case future callof documents should be stronger (override) adjustment,
  but if customer warn us about opposite case.

  If new demand is not fulfilled by
  current product stock and production forecast
  there is a shortage in particular days and we need to rise an alert.
  planner should be notified, 

  if there are locked parts on stock,
  QA task for recovering them should have high priority.

  Scenario: demand increased but fulfilled by current stock level
    Given demand for product "XY":
      | 0 | 100 | 300 | 0 | 0 | 0 | 0 |
    Given current stock of proper parts of "XY" is 750
    When demand of "XY" is adjusted for "tomorrow" to 400
    Then demand is changed
    Then no shortage was found
    Then current demands are
      | 0 | 400 | 300 | 0 | 0 | 0 | 0 |

  Scenario: demand increased, shortage is found
    Given demand for product "XY":
      | 0 | 100 | 300 | 0 | 0 | 0 | 0 |
    Given no production of "XY" is planned
    Given current stock of proper parts of "XY" is 350
    When demand of "XY" is adjusted for "tomorrow" to 400
    Then demand is changed
    Then shortage of 250 parts for "day after tomorrow" is found
    Then current demands are
      | 0 | 400 | 300 | 0 | 0 | 0 | 0 |

  Scenario: demand increased, but stock forecast including production will be enough
    Given demand for product "XY":
      | 0 | 100 | 300 | 0 | 0 | 0 | 0 |
    Given planned production of "XY":
      | 300 | 0 | 0 | 0 | 0 | 0 | 0 |
    Given current stock of proper parts of "XY" is 450
    When demand of "XY" is adjusted for "tomorrow" to 400
    Then demand is changed
    Then no shortage was found
    Then current demands are
      | 0 | 400 | 300 | 0 | 0 | 0 | 0 |

  Scenario: adjustment will be stronger that future callof
    Given demand for product "XY":
      | 0 | 100 | 0 | 0 | 0 | 0 | 0 |
    Given current stock of proper parts of "XY" is 750
    When demand of "XY" is adjusted for "tomorrow" to 400
    Then demand is changed
    When new callof document for product "XY" contains:
      | 0 | 100 | 0 | 0 | 0 | 0 | 0 |
    Then demand is not changed
    Then current demands are
      | 0 | 100 | 0 | 0 | 0 | 0 | 0 |
