package com.cryptoflio.portfolio_service.Controller;

import com.cryptoflio.portfolio_service.ServiceImpl.SnapshotServiceImpl;
import com.cryptoflio.portfolio_service.Dto.BaseApiResponse;
import com.cryptoflio.portfolio_service.Dto.Mapper.SnapshotMapper;
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
@RequestMapping("api/v1/snapshots")
@RequiredArgsConstructor
public class SnapshotController {

    private final Logger logger = LoggerFactory.getLogger(SnapshotController.class);

    private final SnapshotServiceImpl snapshotService;

    private final SnapshotMapper snapshotMapper;

    @GetMapping("")
    public ResponseEntity<BaseApiResponse<List<SnapshotDTO>>> getSnapshots(@RequestHeader("loggedInUser") String userEmail,@RequestParam(value = "period" , defaultValue = "DAY" , required = false) Period period)
    {
        List<PortfolioSnapshot> snapshotList = snapshotService.getPortfolioSnapshotsByUserEmail(userEmail, period );

        List<SnapshotDTO> snapshotDTOList = snapshotList.stream()
                .map(snapshotMapper::toSnapshotDto)
                .toList();

        if (snapshotDTOList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(snapshotDTOList, "No snapshots found"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(snapshotDTOList, "Snapshots found"));
    }
}
