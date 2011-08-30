

///globals
//zapisuje aktualną informację o błędach
var info = ""

function validateName(name){
    if (name.length < 3) {
        info = "Za mało liter";
        return false;
    } 
    if (name[0].toLowerCase() == name[0]) {
        info = "Zacznij wielką literą"
        return false;
    }
    return true;
}
           
function validatePhone(phone){
    var isError = false;
    if(phone.length < 9) {
        info = "Numer zbyt krótki";
        isError = true;
    } 
    else {
        if (phone.match(/\D/)) {
            info = "Dozwolone tylko liczby bez spacji"
            isError = true;
        }
    }
    return !isError;
}
           
function validateEmail(email) {
    var isError = false;
    var array = email.split('@');
    if (array.length < 2) {
        isError = true;
    }
    else {
        if (array[0].length < 2 || array[1].length < 5) {
            isError = true;
        }
        else {
            var nextArray = array[1].split('.');
            if(nextArray.length < 2){
                isError = true;
            }
            else {
                if(nextArray[0].length < 2 || nextArray[1].length < 2){
                    isError = true;
                }
            }
        }
    }
    info ="Błędny email";
    return !isError;
}
          
function  validateNewValue(newValue){
    var column = columnNumber();
    var isValid = true;
    switch (column){
        case 1:
        case 2:
            isValid = validateName(newValue);
            $('#editedInfo').text(info);
            break;
        case 3:
            isValid = validateEmail(newValue);
            $('#editedInfo').text(info);
            break;
        case 5:
            isValid = validatePhone(newValue);
            $('#editedInfo').text(info);
            break;
        default:
            break;
    }
    return isValid;
}

function mkPass(){
    return Math.floor(Math.random() * 89999999) + 10000000;
}

//Sprawdzanie numeru aktualnie edytowanej kolumny 
function columnNumber(){
    var nr = -1;
    $editedTd.parent('tr').children('td').each(function(index){
        if (this.id == 'EDITED') nr = index;
    });
    return nr;
}
   
//skreślanie i przywracanie użytkownika                  
function toggleScratch(elem){
    var $input = $(elem);
    var $tr =  $input.parent('td').parent('tr');
    var isScratched = $tr.hasClass('scratched');
    if (isScratched) {
        if(confirm('Chcesz przywrócić użytkownika?')) {
            $tr.removeClass('scratched');
            $input.attr('src','/style/images/delico.png');
            addDataToUsersArray($tr);
        } 
    }
    else {
        if(confirm('Chcesz skreślić użytkownika?')) {
            $tr.addClass('scratched'); 
            $input.attr('src','/style/images/addico.png'); 
            addDataToUsersArray($tr);
        }      
    }   
}

//usunięcie dodanego w danej sesji
function deleteUser(elem) {
    var $tr = $(elem).parent('td').parent('tr');
    var id = parseInt($tr.children('td:first').text());
    for (i in users){
        if (users[i].id == id){
            users.splice(i,1);
        }
    }
    $tr.remove();
    incraseToSaveInfo();
}
   
function incraseToSaveInfo(){
    var length = users.length;
    if (length > 0) $('#saveButton').show(300);
    $('#editRows').val(length.toString());
              
}
           
function clearFormsAdd(){
    $('#addForm').children('table').children('tr').childern('td').
    children('input').val('');
    clearFormsAddInfo();
}
function clearFormsAddInfo() {
    $('#addForm').children('table').children('tr').childern('td').
    children('span').text('');
}