package com.cryptoflio.portfolio_service.Controller;

import com.cryptoflio.portfolio_service.Entites.ChartType;
import com.cryptoflio.portfolio_service.ServiceImpl.ChartServiceImpl;
import com.cryptoflio.portfolio_service.ServiceImpl.SnapshotServiceImpl;
import com.cryptoflio.portfolio_service.Dto.BarChartResponseDTO;
import com.cryptoflio.portfolio_service.Dto.BaseApiResponse;
import com.cryptoflio.portfolio_service.Dto.Mapper.SnapshotMapper;
import com.cryptoflio.portfolio_service.Dto.PieChartResponseDTO;
import com.cryptoflio.portfolio_service.Dto.SnapshotDTO;
import com.cryptoflio.portfolio_service.Entites.Period;
import com.cryptoflio.portfolio_service.Entites.PortfolioSnapshot;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/charts")
@RequiredArgsConstructor
public class ChartsController {
    private final Logger logger = LoggerFactory.getLogger(ChartsController.class);

    private final SnapshotServiceImpl snapshotService;


    private final SnapshotMapper snapshotMapper;

    private final ChartServiceImpl chartService;


    @GetMapping("/line-chart-snapshots")
    public ResponseEntity<BaseApiResponse<List<SnapshotDTO>>> getSnapshots(@RequestHeader("loggedInUser") String userEmail , @RequestParam(value = "period" , defaultValue = "DAY" , required = false) Period period) {
        List<PortfolioSnapshot> snapshotList = snapshotService.getPortfolioSnapshotsByUserEmail(userEmail , period);

        List<SnapshotDTO> snapshotDTOList = snapshotList.stream()
                .map(snapshotMapper::toSnapshotDto)
                .toList();

        if (snapshotDTOList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(snapshotDTOList, "No snapshots found"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(snapshotDTOList, "Snapshots found"));
    }


    @GetMapping("/pie-chart")
    public ResponseEntity<BaseApiResponse<List<PieChartResponseDTO>>> getPortfolioAssetsForPieChart(@RequestHeader("loggedInUser") String userEmail) {

        List<PieChartResponseDTO> response = chartService.getPortfolioAssetsForPieChart(userEmail);
        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(response, "No Pie Chart data found"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(response, "Pie Chart data found"));

    }


    @GetMapping("/bar-chart")
    public ResponseEntity<BaseApiResponse<List<BarChartResponseDTO>>> getBarChartData(@RequestHeader("loggedInUser") String userEmail, @RequestParam(required = false, defaultValue = "VALUE") ChartType chartType) {

        List<BarChartResponseDTO> listDTO = chartService.getBarChartData(userEmail, chartType);

        if (listDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(listDTO, "No Asset found for user " + userEmail));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(listDTO, "found successfully"));

    }

}
