package com.cryptoflio.portfolio_service.Controller;

import com.cryptoflio.portfolio_service.ServiceImpl.SnapshotServiceImpl;
import com.cryptoflio.portfolio_service.Dto.Mapper.SnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/snapshots")
@RequiredArgsConstructor
public class PortfolioSnapshot {

    private final SnapshotServiceImpl service;

    private final SnapshotMapper mapper;

//    @GetMapping("/snapshot")
//    public ResponseEntity<SnapshotDTO> getPortfolioSnapshot(@RequestParam Long id) {
//        // TODO: Implement logic to fetch portfolio snapshot
//        var dto = SnapshotMapper.toSnapshotDto();
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }


}
