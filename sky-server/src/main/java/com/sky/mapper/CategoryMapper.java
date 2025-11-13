package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_User, update_User) " +
            "values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void createCategory(Category category);

    @Delete("delete from category where id = #{id}")
    void deleteCategory(Long id);

    void updateCategory(Category category);

    Page<Category> selectByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    @Select("select * from category where type = #{type}")
    List<Category> selectCategoryByType(Integer type);
}
