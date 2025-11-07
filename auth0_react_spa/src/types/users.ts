export type User = {
  userId: string;
  idTypeId?: string;
  idNumber?: string;
  firstName: string;
  secondName?: string;
  firstSurname: string;
  secondSurname?: string;
  fullName: string;
  email: string;
  mobileNumber?: string;
  emailConfirmed: boolean;
  mobileNumberConfirmed: boolean;
  homeCityId?: string;
};
