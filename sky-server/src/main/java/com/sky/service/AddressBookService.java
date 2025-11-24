package com.sky.service;

import com.sky.context.CurrentContext;
import com.sky.entity.AddressBook;
import com.sky.exception.BaseException;
import com.sky.mapper.AddressBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookService {

    private static final Integer DEFAULT_ADDRESS_BOOK = 1;
    private static final Integer NOT_DEFAULT_ADDRESS_BOOK = 0;

    @Autowired
    private AddressBookMapper addressBookMapper;

    public void addAddressBook(AddressBook addressBook) {
        addressBook.setUserId(CurrentContext.getCurrent());
        addressBook.setIsDefault(NOT_DEFAULT_ADDRESS_BOOK);
        addressBookMapper.addAddressBook(addressBook);
    }

    public void updateAddressBook(AddressBook addressBook) {
        addressBookMapper.updateAddressBook(addressBook);
    }

    public void deleteAddressBookById(Long id) {
        addressBookMapper.deleteAddressBookById(id);
    }

    public AddressBook selectAddressBookById(Long id) {
        return addressBookMapper.selectAddressBookById(id);
    }

    public List<AddressBook> selectAllAddressBooks() {
        AddressBook addressBook = AddressBook.builder()
                .userId(CurrentContext.getCurrent()).build();
        return addressBookMapper.selectAllAddressBooks(addressBook);
    }

    @Transactional
    public void updateDefaultAddressBook(AddressBook addressBook) {
        addressBookMapper.updateAllAddressBooksDefault(CurrentContext.getCurrent());
        addressBook.setIsDefault(DEFAULT_ADDRESS_BOOK);
        addressBookMapper.updateAddressBook(addressBook);
    }

    public AddressBook selectDefaultAddressBook() {
        AddressBook addressBook = AddressBook.builder()
                .userId(CurrentContext.getCurrent()).isDefault(DEFAULT_ADDRESS_BOOK).build();
        List<AddressBook> addressBooks = addressBookMapper.selectAllAddressBooks(addressBook);
        if (addressBooks != null && addressBooks.size() == 1) {
            return addressBooks.getFirst();
        } else throw new BaseException("没有查询到默认地址");
    }
}
