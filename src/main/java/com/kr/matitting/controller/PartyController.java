package com.kr.matitting.controller;

import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PartyController {
    private final PartyService partyService;

    @GetMapping({"/main/search", "/main/search/{page}"})
    public ResponseEntity partySearch(PartySearchCondDto partySearchCondDto,
                                      @PathVariable(name = "page") Optional<Integer> page,
                                      @RequestParam int limit,
                                      @RequestParam Map<String, String> orders) {

        //TODO: offset, limit 활용법 제대로 익히기
        PageRequest pageable = PageRequest.of(!page.isPresent() ? 0 : page.get(), limit);
        orders.forEach((orderColumn, orderType) -> {
            pageable.withSort(
                    (orderType == "desc") ? Sort.Direction.DESC : Sort.Direction.ASC, orderColumn);
        });

        partyService.getPartyPage(partySearchCondDto, pageable);

        return null;
    }
}
