import React from "react";
import PropTypes from "prop-types";
import { Chart } from "react-charts";

const StatisticsDashboardDifferenceBarChart = ({ records }) => {
  const uniqueDates = [...new Set(records.map((record) => record.date))].sort();

  const totalSum = records.reduce((sum, record) => sum + record.difference, 0);

  const totalByRegion = records.reduce((groups, record) => {
    const key = record.region.id;
    groups[key] = { ...(groups[key] || { regionId: key, difference: 0 }) };
    groups[key].difference += record.difference;
    return groups;
  }, {});

  const sortedTotalByRegion = Object.values(totalByRegion).sort(
    (r1, r2) => r2.difference - r1.difference
  );

  sortedTotalByRegion.forEach(
    (record, index) => (totalByRegion[record.regionId].top = index + 1)
  );

  const data = React.useMemo(() => {
    return uniqueDates.map((date) => ({
      label: date,
      datums: records
        .filter((record) => record.date === date)
        .sort(
          (r1, r2) =>
            totalByRegion[r2.region.id].difference -
            totalByRegion[r1.region.id].difference
        )
        .map((record) => ({
          x:
            totalByRegion[record.region.id].top +
            ". " +
            record.region.name +
            ` (${totalByRegion[record.region.id].difference})`,
          y: record.difference,
        })),
    }));
  });

  const series = React.useMemo(
    () => ({
      type: "bar",
    }),
    []
  );

  const axes = React.useMemo(() => [
    { primary: true, type: "ordinal", position: "left" },
    { position: "bottom", type: "linear", stacked: true },
  ]);

  return (
    <>
      <h2 style={{ marginLeft: "150px" }}>
        {uniqueDates[0]}
        {uniqueDates.length > 1
          ? ` - ${uniqueDates[uniqueDates.length - 1]}`
          : ""}
        <br />({totalSum})
      </h2>
      <Chart data={data} series={series} axes={axes} tooltip />
    </>
  );
};

StatisticsDashboardDifferenceBarChart.propTypes = {
  records: PropTypes.array.isRequired,
};

export default StatisticsDashboardDifferenceBarChart;
