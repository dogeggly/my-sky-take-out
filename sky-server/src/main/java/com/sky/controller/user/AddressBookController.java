package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Result addAddressBook(@RequestBody AddressBook addressBook) {
        log.info("添加地址簿: {}", addressBook);
        addressBookService.addAddressBook(addressBook);
        return Result.success();
    }

    @PutMapping
    public Result updateAddressBook(@RequestBody AddressBook addressBook) {
        log.info("修改地址簿: {}", addressBook);
        addressBookService.updateAddressBook(addressBook);
        return Result.success();
    }

    //前端url写得有问题
    @DeleteMapping("/")
    public Result deleteAddressBookById(Long id) {
        log.info("根据id删除地址簿: {}", id);
        addressBookService.deleteAddressBookById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressBook> selectAddressBookById(@PathVariable Long id) {
        log.info("根据id查询地址簿: {}", id);
        AddressBook addressBook = addressBookService.selectAddressBookById(id);
        return Result.success(addressBook);
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> selectAllAddressBooks() {
        log.info("查询所有地址簿");
        List<AddressBook> addressBooks = addressBookService.selectAllAddressBooks();
        return Result.success(addressBooks);
    }

    @PutMapping("/default")
    public Result updateDefaultAddressBook(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址簿: {}", addressBook);
        addressBookService.updateDefaultAddressBook(addressBook);
        return Result.success();
    }

    @GetMapping("/default")
    public Result<AddressBook> selectDefaultAddressBook() {
        log.info("查询默认地址簿");
        AddressBook addressBook = addressBookService.selectDefaultAddressBook();
        return Result.success(addressBook);
    }
}
