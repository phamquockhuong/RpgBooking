package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Coupon;
import com.example.RpgBooking.repository.CouponRepository;
import com.example.RpgBooking.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepo;
    private final UserRepository userRepo;

    @GetMapping
    public String index(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 15) Pageable pageable,
            Model model
    ) {

        Page<Coupon> couponPage;

        if (keyword != null && !keyword.isBlank()) {
            couponPage = couponRepo.findByCodeContainingIgnoreCase(
                    keyword,
                    pageable
            );
        } else {
            couponPage = couponRepo.findAll(pageable);
        }

        model.addAttribute("couponPage", couponPage);
        model.addAttribute("keyword", keyword);

        return "admin/coupons/index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {

        model.addAttribute("coupon", new Coupon());
        model.addAttribute("users", userRepo.findAll());

        return "admin/coupons/add";
    }

    @PostMapping("/add")
    public String saveAdd(
            @Valid @ModelAttribute Coupon coupon,
            BindingResult result,
            Model model
    ) {

        if (couponRepo.existsByCode(coupon.getCode())) {
            result.rejectValue(
                    "code",
                    "",
                    "Mã coupon đã tồn tại"
            );
        }

        if (coupon.getEndDate().isBefore(coupon.getStartDate())) {
            result.rejectValue(
                    "endDate",
                    "",
                    "Ngày kết thúc phải lớn hơn ngày bắt đầu"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("users", userRepo.findAll());
            return "admin/coupons/add";
        }

        couponRepo.save(coupon);

        return "redirect:/admin/coupons";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model
    ) {

        Coupon coupon = couponRepo.findById(id)
                .orElseThrow();

        model.addAttribute("coupon", coupon);
        model.addAttribute("users", userRepo.findAll());

        return "admin/coupons/edit";
    }

    @PostMapping("/edit/{id}")
    public String saveEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute Coupon coupon,
            BindingResult result,
            Model model
    ) {

        Coupon existing = couponRepo.findById(id)
                .orElseThrow();

        if (!existing.getCode().equals(coupon.getCode())
                && couponRepo.existsByCode(coupon.getCode())) {

            result.rejectValue(
                    "code",
                    "",
                    "Mã coupon đã tồn tại"
            );
        }

        if (coupon.getEndDate().isBefore(coupon.getStartDate())) {
            result.rejectValue(
                    "endDate",
                    "",
                    "Ngày kết thúc phải lớn hơn ngày bắt đầu"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("users", userRepo.findAll());
            return "admin/coupons/edit";
        }

        coupon.setId(id);

        couponRepo.save(coupon);

        return "redirect:/admin/coupons";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        couponRepo.deleteById(id);

        return "redirect:/admin/coupons";
    }
}